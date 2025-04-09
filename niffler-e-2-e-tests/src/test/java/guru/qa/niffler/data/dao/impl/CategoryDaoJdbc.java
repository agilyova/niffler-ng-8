package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

  private final static Config CFG = Config.getInstance();

  @Override
  public CategoryEntity create(CategoryEntity category) {
    try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
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
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM category WHERE id = ?"
      )) {
        ps.setObject(1, id);
        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setName(rs.getString("name"));
            ce.setUsername(rs.getString("username"));
            ce.setArchived(rs.getBoolean("archived"));
            return Optional.of(ce);
          } else {
            return Optional.empty();
          }
        }
       }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
