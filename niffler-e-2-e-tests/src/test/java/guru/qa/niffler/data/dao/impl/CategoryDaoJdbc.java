package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

  private final Connection connection;

  public CategoryDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public CategoryEntity create(CategoryEntity category) {
    try (PreparedStatement ps = connection.prepareStatement(
      "INSERT INTO category (name, username, archived)" +
        "VALUES (?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, category.getName());
      ps.setString(2, category.getUsername());
      ps.setBoolean(3, category.isArchived());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Cant find id in ResultSet");
        }
      }
      category.setId(generatedKey);
      return category;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (PreparedStatement ps = connection.prepareStatement(
      "SELECT * FROM category WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          CategoryEntity ce = fillCategoryEntity(rs);
          return Optional.of(ce);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
    try (PreparedStatement ps = connection.prepareStatement(
      "SELECT * FROM category " +
        "WHERE username = ? AND name = ?"
    )) {
      ps.setString(1, username);
      ps.setString(2, categoryName);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          CategoryEntity ce = fillCategoryEntity(rs);
          return Optional.of(ce);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<CategoryEntity> findAllByUserName(String username) {
    try (PreparedStatement ps = connection.prepareStatement(
      "SELECT * FROM category WHERE username = ?"
    )) {
      ps.setString(1, username);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        List<CategoryEntity> ceList = new ArrayList<>();
        while (rs.next()) {
          CategoryEntity ce = fillCategoryEntity(rs);
          ceList.add(ce);
        }
        return ceList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<CategoryEntity> findAll() {
    try (PreparedStatement ps = connection.prepareStatement(
      "SELECT * FROM category"
    )) {
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        List<CategoryEntity> resultEntityList = new ArrayList<>();
        while (rs.next()) {
          CategoryEntity ce = fillCategoryEntity(rs);
          resultEntityList.add(ce);
        }
        return resultEntityList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteCategory(CategoryEntity category) {
    try (PreparedStatement ps = connection.prepareStatement(
      "DELETE FROM category WHERE id = ?"
    )) {
      ps.setObject(1, category.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Can't delete entity", e);
    }
  }

  private static CategoryEntity fillCategoryEntity(ResultSet rs) throws SQLException {
    CategoryEntity ce = new CategoryEntity();
    ce.setId(rs.getObject("id", UUID.class));
    ce.setName(rs.getString("name"));
    ce.setUsername(rs.getString("username"));
    ce.setArchived(rs.getBoolean("archived"));
    return ce;
  }
}
