package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {
  private static final Config CFG = Config.getInstance();

  @Override
  public AuthUserEntity create(AuthUserEntity entity) {
    try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
      "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
        " VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
         PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
           "INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {
      userPs.setString(1, entity.getUsername());
      userPs.setString(2, entity.getPassword());
      userPs.setBoolean(3, entity.getEnabled());
      userPs.setBoolean(4, entity.getAccountNonExpired());
      userPs.setBoolean(5, entity.getAccountNonLocked());
      userPs.setBoolean(6, entity.getCredentialsNonExpired());
      userPs.executeUpdate();

      UUID generatedKey;
      try (ResultSet rs = userPs.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
        entity.setId(generatedKey);

        for (AuthorityEntity a : entity.getAuthorities() ) {
          authorityPs.setObject(1, generatedKey);
          authorityPs.setString(2, a.getAuthority().name());
          authorityPs.addBatch();
          authorityPs.clearParameters();
        }
        authorityPs.executeBatch();
        return entity;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
      "SELECT * FROM \"user\" u " +
        "JOIN authority a ON u.id = a.user_id " +
        "WHERE u.id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        AuthUserEntity user = null;
        List<AuthorityEntity> authorityEntities = new ArrayList<>();
        while (rs.next()) {
          if (user == null) {
            user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
          }
          AuthorityEntity ae = new AuthorityEntity();
          ae.setUser(user);
          ae.setId(rs.getObject("a.id", UUID.class));
          ae.setAuthority(Authority.valueOf(rs.getString("authority")));
          authorityEntities.add(ae);
        }
        if (user == null) {
          return Optional.empty();
        } else {
          user.setAuthorities(authorityEntities);
          return Optional.of(user);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
