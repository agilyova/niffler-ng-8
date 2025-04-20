package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.data.mapper.UserDataUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDAOSpringJdbc implements UserdataUserDAO {

  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity create(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
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
      , kh);
    user.setId((UUID) kh.getKeys().get("id"));
    System.out.println(user);
    return user;
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    try {
      return Optional.ofNullable(
        jdbcTemplate.queryForObject(
          "SELECT * FROM \"user\" WHERE id = ?",
          UserDataUserEntityRowMapper.instance,
          id
        ));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    return Optional.ofNullable(
      jdbcTemplate.queryForObject(
        "SELECT * FROM \"user\" WHERE username = ?",
        UserDataUserEntityRowMapper.instance,
        username
      ));
  }

  @Override
  public List<UserEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    return jdbcTemplate.query(
      "SELECT * FROM \"user\"",
      UserDataUserEntityRowMapper.instance
    );
  }

  @Override
  public void delete(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    jdbcTemplate.update(
      "DELETE FROM \"user\" WHERE id = ?",
      user.getId()
    );
  }
}
