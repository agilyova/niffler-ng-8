package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.data.mapper.UserDataUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDAOSpringJdbc implements UserdataUserDAO {

  private final DataSource dataSource;

  public UserdataUserDAOSpringJdbc(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public UserEntity createUser(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(
      connection -> {
        PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
        );
          ps.setString(1, user.getUsername());
          ps.setString(2, user.getCurrency().name());
          ps.setString(3, user.getFirstname());
          ps.setString(4, user.getSurname());
          ps.setBytes(5, user.getPhoto());
          ps.setBytes(6, user.getPhotoSmall());
          ps.setString(7, user.getFullname());
          return ps;
      }
    ,kh);
    user.setId((UUID) kh.getKeys().get("id"));
    return user;
  }

  @Override
  public Optional<UserEntity> findUserById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return Optional.ofNullable(
      jdbcTemplate.queryForObject(
        "SELECT * FROM \"user\" WHERE id = ?",
        UserDataUserEntityRowMapper.instance,
        id
      ));
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return Optional.ofNullable(
      jdbcTemplate.queryForObject(
        "SELECT * FROM \"user\" WHERE username = ?",
        UserDataUserEntityRowMapper.instance,
        username
      ));
  }

  @Override
  public void delete(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.update(
      con -> {
        PreparedStatement ps = con.prepareStatement(
          "DELETE FROM \"user\" WHERE id = ?"
        );
        ps.setObject(1, user.getId());
        return ps;
      }
    );
  }
}
