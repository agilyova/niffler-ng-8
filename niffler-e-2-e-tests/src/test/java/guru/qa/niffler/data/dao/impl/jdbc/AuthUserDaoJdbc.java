package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserDaoJdbc implements AuthUserDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public AuthUserEntity create(AuthUserEntity entity) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
      "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
        " VALUES (?, ?, ?, ?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, entity.getUsername());
      ps.setString(2, entity.getPassword());
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
  public AuthUserEntity update(AuthUserEntity entity) {
    try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
      "UPDATE \"user\" SET " +
        "username = ?," +
        "password = ?," +
        "enabled = ?," +
        "account_non_expired = ?," +
        "account_non_locked = ?," +
        "credentials_non_expired = ?" +
        "WHERE id = ?"
    )) {
      ps.setString(1, entity.getUsername());
      ps.setString(2, entity.getPassword());
      ps.setBoolean(3, entity.getEnabled());
      ps.setBoolean(4, entity.getAccountNonExpired());
      ps.setBoolean(5, entity.getAccountNonLocked());
      ps.setBoolean(6, entity.getCredentialsNonExpired());
      ps.setObject(7, entity.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return entity;
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
      "SELECT * FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          AuthUserEntity result = fillAuthUserEntity(rs);
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
  public List<AuthUserEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
      "SELECT * FROM \"user\""
    )) {
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        List<AuthUserEntity> resultEntityList = new ArrayList<>();
        while (rs.next()) {
          AuthUserEntity aue = fillAuthUserEntity(rs);
          resultEntityList.add(aue);
        }
        return resultEntityList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(AuthUserEntity entity) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
      "DELETE FROM \"user\" WHERE id =  ?"
    )) {
      ps.setObject(1, entity.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static AuthUserEntity fillAuthUserEntity(ResultSet rs) throws SQLException {
    AuthUserEntity result = new AuthUserEntity();
    result.setId(rs.getObject("id", UUID.class));
    result.setUsername(rs.getString("username"));
    result.setPassword(rs.getString("password"));
    result.setEnabled(rs.getBoolean("enabled"));
    result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
    result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
    result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    return  result;
  }
}
