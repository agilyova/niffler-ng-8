package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {
  private final Connection connection;

  public AuthUserDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public AuthUserEntity create(AuthUserEntity entity) {
    try (PreparedStatement ps = connection.prepareStatement(
      "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
        " VALUES (?, ?, ?, ?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, entity.getUsername());
      ps.setString(2, PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(entity.getPassword()));
      ps.setBoolean(3, entity.getEnabled());
      ps.setBoolean(4, entity.getAccountNonExpired());
      ps.setBoolean(5, entity.getAccountNonLocked());
      ps.setBoolean(6, entity.getCredentialsNonExpired());
      ps.executeUpdate();

      UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
        entity.setId(generatedKey);
        return entity;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = connection.prepareStatement(
      "SELECT * FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          AuthUserEntity result = new AuthUserEntity();
          result.setId(rs.getObject("id", UUID.class));
          result.setUsername(rs.getString("username"));
          result.setPassword(rs.getString("password"));
          result.setEnabled(rs.getBoolean("enabled"));
          result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          return Optional.of(result);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(AuthUserEntity entity) {
    try (PreparedStatement ps = connection.prepareStatement(
      "DELETE FROM \"user\" WHERE id =  ?"
    )) {
      ps.setObject(1, entity.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
