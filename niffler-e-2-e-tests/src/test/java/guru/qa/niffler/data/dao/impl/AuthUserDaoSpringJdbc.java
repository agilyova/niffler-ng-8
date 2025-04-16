package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc implements AuthUserDao {
  DataSource dataSource;

  public AuthUserDaoSpringJdbc(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public AuthUserEntity create(AuthUserEntity entity) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(
      con -> {
        PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
            " VALUES (?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, entity.getUsername());
        ps.setString(2, PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(entity.getPassword()));
        ps.setBoolean(3, entity.getEnabled());
        ps.setBoolean(4, entity.getAccountNonExpired());
        ps.setBoolean(5, entity.getAccountNonLocked());
        ps.setBoolean(6, entity.getCredentialsNonExpired());
        return ps;
      },
      kh
    );
    entity.setId((UUID) kh.getKeys().get("id"));
    return entity;
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return Optional.ofNullable(
      jdbcTemplate.queryForObject(
        "SELECT * FROM \"user\" WHERE id = ?",
        AuthUserEntityRowMapper.instance,
        id
      )
    );
  }

  @Override
  public List<AuthUserEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return jdbcTemplate.query(
      "SELECT * FROM \"user\"",
      AuthUserEntityRowMapper.instance
    );
  }

  @Override
  public void delete(AuthUserEntity entity) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.update(
      "DELETE FROM \"user\" WHERE id = ?",
      entity.getId()
    );
  }
}
