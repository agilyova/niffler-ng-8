package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;

import java.sql.*;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private final Connection connection;

  public AuthAuthorityDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public AuthorityEntity create(AuthorityEntity entity) {
    try (PreparedStatement ps = connection.prepareStatement(
      "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
      Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setObject(1, entity.getUserId());
      ps.setString(2, entity.getAuthority().name());
      ps.executeUpdate();

      final UUID generatedKey;
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
  public void delete(AuthorityEntity entity) {
    try (PreparedStatement ps = connection.prepareStatement(
      "DELETE FROM authority WHERE id =  ?"
    )) {
      ps.setObject(1, entity.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
