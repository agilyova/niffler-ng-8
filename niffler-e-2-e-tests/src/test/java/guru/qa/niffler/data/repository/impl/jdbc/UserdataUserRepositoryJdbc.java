package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userData.FriendshipEntity;
import guru.qa.niffler.data.entity.userData.FriendshipStatus;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.enums.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity create(UserEntity user) {
    try (PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS)
    ) {
      userPs.setString(1, user.getUsername());
      userPs.setString(2, user.getCurrency().name());
      userPs.setString(3, user.getFirstname());
      userPs.setString(4, user.getSurname());
      userPs.setBytes(5, user.getPhoto());
      userPs.setBytes(6, user.getPhotoSmall());
      userPs.setString(7, user.getFullname());
      userPs.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = userPs.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
        user.setId(generatedKey);
        return user;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    try (PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "SELECT * FROM \"user\" WHERE id = ?");
         PreparedStatement friendshipRequestsPs = holder(CFG.userdataJdbcUrl()).connection()
           .prepareStatement("SELECT * FROM friendship f " +
             "JOIN \"user\" u ON f.addressee_id = u.id " +
             "WHERE requester_id = ?");
         PreparedStatement friendshipInvitationsPs = holder(CFG.userdataJdbcUrl()).connection()
           .prepareStatement("SELECT * FROM friendship f " +
             "JOIN \"user\" u ON f.requester_id = u.id " +
             "WHERE addressee_id = ? AND status = ? ")
    ) {
      userPs.setObject(1, id);
      userPs.execute();
      UserEntity ue = null;
      try (ResultSet rs = userPs.getResultSet()) {
        if (rs.next()) {
          ue = fillUserEntity(rs);
        }
      }

      friendshipRequestsPs.setObject(1, id);
      friendshipRequestsPs.execute();
      List<FriendshipEntity> friendshipEntities = new ArrayList<>();
      try (ResultSet rs = friendshipRequestsPs.getResultSet()) {
        while (rs.next()) {
          UserEntity addressee = fillUserEntity(rs);
          FriendshipEntity fe = fillFriendshipEntity(rs, ue, addressee);
          friendshipEntities.add(fe);
        }
      }

      friendshipInvitationsPs.setObject(1, id);
      friendshipInvitationsPs.setString(2, FriendshipStatus.PENDING.name());
      friendshipInvitationsPs.execute();
      List<FriendshipEntity> friendshipInvEntities = new ArrayList<>();
      try (ResultSet rs = friendshipInvitationsPs.getResultSet()) {
        while (rs.next()) {
          UserEntity requester = fillUserEntity(rs);
          FriendshipEntity fe = fillFriendshipEntity(rs, requester, ue);
          friendshipInvEntities.add(fe);
        }
      }

      if (ue != null) {
        ue.setFriendshipRequests(friendshipEntities);
        ue.setFriendshipAddressees(friendshipInvEntities);
        return Optional.of(ue);
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    return Optional.empty();
  }

  @Override
  public void addInvitation(UserEntity requester, UserEntity addressee) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "INSERT INTO friendship (requester_id, addressee_id, status) " +
        "VALUES (?, ?, ?) " +
        "ON CONFLICT (requester_id, addressee_id) " +
        "DO UPDATE SET status = ?, created_date = NOW()"
    )) {
      ps.setObject(1, requester.getId());
      ps.setObject(2, addressee.getId());
      ps.setString(3, FriendshipStatus.PENDING.name());
      ps.setString(4, FriendshipStatus.PENDING.name());
      ps.execute();

      requester.addFriends(FriendshipStatus.PENDING, addressee);
      addressee.addInvitations(requester);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "INSERT INTO friendship (requester_id, addressee_id, status) " +
        "VALUES (?, ?, ?)" +
        "ON CONFLICT (requester_id, addressee_id) " +
        "DO UPDATE SET status = ?, created_date = NOW()"
    )) {
      ps.setObject(1, requester.getId());
      ps.setObject(2, addressee.getId());
      ps.setString(3, FriendshipStatus.ACCEPTED.name());
      ps.setString(4, FriendshipStatus.ACCEPTED.name());
      ps.addBatch();

      ps.setObject(1, addressee.getId());
      ps.setObject(2, requester.getId());
      ps.setString(3, FriendshipStatus.ACCEPTED.name());
      ps.setString(4, FriendshipStatus.ACCEPTED.name());
      ps.addBatch();

      ps.executeBatch();

      requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
      addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static FriendshipEntity fillFriendshipEntity(ResultSet rs, UserEntity requester, UserEntity addressee) throws SQLException {
    FriendshipEntity fe = new FriendshipEntity();
    fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
    fe.setCreatedDate(rs.getDate("created_date"));
    fe.setRequester(requester);
    fe.setAddressee(addressee);
    return fe;
  }

  private static UserEntity fillUserEntity(ResultSet rs) throws SQLException {
    UserEntity ue = new UserEntity();
    ue.setId(rs.getObject("id", UUID.class));
    ue.setUsername(rs.getString("username"));
    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    ue.setFirstname(rs.getString("firstname"));
    ue.setSurname(rs.getString("surname"));
    ue.setPhoto(rs.getBytes("photo"));
    ue.setPhotoSmall(rs.getBytes("photo_small"));
    ue.setFullname(rs.getString("full_name"));
    return ue;
  }
}
