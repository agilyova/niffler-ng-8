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
  public void create(AuthorityEntity entity) {
    try (PreparedStatement ps = connection.prepareStatement(
      "INSERT INTO authority (user_id, authority) VALUES (?, ?)"
    )) {
      ps.setObject(1, entity.getUserId());
      ps.setString(2, entity.getAuthority().name());
      ps.executeUpdate();
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
