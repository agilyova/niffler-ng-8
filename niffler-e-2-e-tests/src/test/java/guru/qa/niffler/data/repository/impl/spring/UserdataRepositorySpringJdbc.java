package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.dao.impl.spring.UserdataUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.userData.FriendshipEntity;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.data.mapper.UserDataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userData.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userData.FriendshipStatus.PENDING;

public class UserdataRepositorySpringJdbc implements UserdataUserRepository {

  private static final Config CFG = Config.getInstance();

  UserdataUserDAO userdataUserDAO = new UserdataUserDAOSpringJdbc();

  @Override
  public UserEntity create(UserEntity user) {
    return userdataUserDAO.create(user);
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    Optional<UserEntity> userOpt = userdataUserDAO.findById(id);
    if (userOpt.isEmpty()) {
      return Optional.empty();
    }

    UserEntity userEntity = userOpt.get();

    List<UserEntity> listOfAcceptedFriends = jdbcTemplate.query(
      "SELECT * FROM friendship f " +
        "JOIN \"user\" u ON f.addressee_id = u.id " +
        "WHERE requester_id = ? AND status = ? ",
      UserDataUserEntityRowMapper.instance,
      id, ACCEPTED.name()
    );

    List<UserEntity> listOfPendingFriends = jdbcTemplate.query(
      "SELECT * FROM friendship f " +
        "JOIN \"user\" u ON f.addressee_id = u.id " +
        "WHERE requester_id = ? AND status = ? ",
      UserDataUserEntityRowMapper.instance,
      id, PENDING.name()
    );

    List<UserEntity> listInvitations = jdbcTemplate.query(
      "SELECT * FROM friendship f " +
        "JOIN \"user\" u ON f.requester_id = u.id " +
        "WHERE addressee_id = ? AND status = ? ",
      UserDataUserEntityRowMapper.instance,
      id, PENDING.name()
    );

    userEntity.addInvitations(listInvitations.toArray(UserEntity[]::new));
    userEntity.addFriends(ACCEPTED, listOfAcceptedFriends.toArray(UserEntity[]::new));
    userEntity.addFriends(PENDING, listOfPendingFriends.toArray(UserEntity[]::new));

    return Optional.of(userEntity);
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    Optional<UserEntity> userOpt = userdataUserDAO.findByUsername(username);
    if (userOpt.isEmpty()) {
      return Optional.empty();
    }

    UserEntity userEntity = userOpt.get();

    List<UserEntity> listOfAcceptedFriends = jdbcTemplate.query(
      "SELECT * FROM friendship f " +
        "JOIN \"user\" u ON f.addressee_id = u.id " +
        "WHERE requester_id = ? AND status = ? ",
      UserDataUserEntityRowMapper.instance,
      userEntity.getId(), ACCEPTED.name()
    );

    List<UserEntity> listOfPendingFriends = jdbcTemplate.query(
      "SELECT * FROM friendship f " +
        "JOIN \"user\" u ON f.addressee_id = u.id " +
        "WHERE requester_id = ? AND status = ? ",
      UserDataUserEntityRowMapper.instance,
      userEntity.getId(), PENDING.name()
    );

    List<UserEntity> listInvitations = jdbcTemplate.query(
      "SELECT * FROM friendship f " +
        "JOIN \"user\" u ON f.requester_id = u.id " +
        "WHERE addressee_id = ? AND status = ? ",
      UserDataUserEntityRowMapper.instance,
      userEntity.getId(), PENDING.name()
    );

    userEntity.addInvitations(listInvitations.toArray(UserEntity[]::new));
    userEntity.addFriends(ACCEPTED, listOfAcceptedFriends.toArray(UserEntity[]::new));
    userEntity.addFriends(PENDING, listOfPendingFriends.toArray(UserEntity[]::new));

    return Optional.of(userEntity);
  }

  @Override
  public UserEntity update(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    jdbcTemplate.update(con -> {
      PreparedStatement userPs = con.prepareStatement(
        "UPDATE \"user\" SET " +
          "username = ?," +
          "currency = ?," +
          "firstname = ?," +
          "surname = ?," +
          "photo = ?," +
          "photo_small = ?," +
          "full_name = ? " +
          "WHERE id = ?"
      );
      userPs.setString(1, user.getUsername());
      userPs.setString(2, user.getCurrency().name());
      userPs.setString(3, user.getFirstname());
      userPs.setString(4, user.getSurname());
      userPs.setBytes(5, user.getPhoto());
      userPs.setBytes(6, user.getPhotoSmall());
      userPs.setString(7, user.getFullname());
      userPs.setObject(8, user.getId());
      return userPs;
    });

    for (FriendshipEntity fe : user.getFriendshipRequests()) {
      addFriend(fe.getRequester(), fe.getAddressee());
    }

    for (FriendshipEntity fe : user.getFriendshipAddressees()) {
      addInvitation(fe.getRequester(), fe.getAddressee());
    }
    return user;
  }

  @Override
  public void addInvitation(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    jdbcTemplate.update(
      "INSERT INTO friendship (requester_id, addressee_id, status) " +
        "VALUES (?, ?, ?) " +
        "ON CONFLICT (requester_id, addressee_id) " +
        "DO UPDATE SET status = ?, created_date = NOW()",
      requester.getId(), addressee.getId(), PENDING.name(), PENDING.name());
    requester.addFriends(PENDING, addressee);
    addressee.addInvitations(requester);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    List<Object[]> batchArgs = List.of(
      new Object[]{requester.getId(), addressee.getId(), ACCEPTED.name(), ACCEPTED.name()},
      new Object[]{addressee.getId(), requester.getId(), ACCEPTED.name(), ACCEPTED.name()}
    );
    jdbcTemplate.batchUpdate(
      "INSERT INTO friendship (requester_id, addressee_id, status) " +
        "VALUES (?, ?, ?)" +
        "ON CONFLICT (requester_id, addressee_id) " +
        "DO UPDATE SET status = ?, created_date = NOW()",
      batchArgs
    );
    requester.addFriends(ACCEPTED, addressee);
    addressee.addFriends(ACCEPTED, requester);
  }

  @Override
  public void remove(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    jdbcTemplate.update(
      "WITH deleteted_friendship AS " +
        "(DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?)" +
        "DELETE FROM \"user\" WHERE id = ?",
      user.getId(), user.getId(), user.getId()
    );
  }
}
