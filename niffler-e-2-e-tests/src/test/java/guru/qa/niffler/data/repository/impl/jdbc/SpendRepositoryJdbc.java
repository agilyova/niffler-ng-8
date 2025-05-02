package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.enums.CurrencyValues;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

  private static final Config CFG = Config.getInstance();

  CategoryDao categoryDao = new CategoryDaoJdbc();
  SpendDao spendDao = new SpendDaoJdbc();

  @Override
  public SpendEntity createSpend(SpendEntity spend) {

    CategoryEntity category = spend.getCategory();

    if (category.getId() == null) {
      String name = category.getName();
      String username = category.getUsername();

      if (name != null && username != null) {
        category = categoryDao.findByUsernameAndCategoryName(name, username)
          .orElseGet(() -> categoryDao.create(spend.getCategory()));
      }
    }
    spend.setCategory(category);
    return spendDao.create(spend);
  }

  @Override
  public SpendEntity updateSpend(SpendEntity spend) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
      "UPDATE spend " +
        "SET username = ?," +
        "spend_date = ?," +
        "currency = ?," +
        "amount = ?," +
        "description = ?," +
        "category_id = ?" +
        "WHERE id = ?"
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());
      ps.setObject(7, spend.getId());

      ps.executeUpdate();

      return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    return categoryDao.create(category);
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return categoryDao.findById(id);
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
    return categoryDao.findByUsernameAndCategoryName(username, name);
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
      "SELECT s.*," +
        "c.id AS category_id, " +
        "c.name," +
        "c.username," +
        "c.archived" +
        " FROM spend s " +
        "JOIN category c ON s.category_id = c.id " +
        "WHERE s.id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          SpendEntity spendEntity = fillSpendEntity(rs);
          return Optional.of(spendEntity);
        } else {
          return Optional.empty();
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<SpendEntity> findSpendingsByCategory(CategoryEntity categoryEntity) {
    return spendDao.findByCategory(categoryEntity);
  }

  @Override
  public Optional<SpendEntity> findSpendByUsernameAndSpendDescription(String username, String description) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
      "SELECT s.*," +
        "c.id AS category_id, " +
        "c.name," +
        "c.username," +
        "c.archived" +
        " FROM spend s " +
        "JOIN category c ON s.category_id = c.id " +
        "WHERE s.username = ? AND s.description = ?"
    )) {
      ps.setString(1, username);
      ps.setString(2, description);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          SpendEntity spendEntity = fillSpendEntity(rs);
          return Optional.of(spendEntity);
        } else {
          return Optional.empty();
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(SpendEntity spend) {
    spendDao.delete(spend);
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
      "WITH deleted_spend AS (" +
        "DELETE FROM spend WHERE category_id = ?)" +
        "DELETE FROM category WHERE id = ?"
    )) {
      ps.setObject(1, category.getId());
      ps.setObject(2, category.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static SpendEntity fillSpendEntity(ResultSet rs) throws SQLException {
    SpendEntity spendEntity = new SpendEntity();

    CategoryEntity categoryEntity = new CategoryEntity();
    categoryEntity.setId(rs.getObject("category_id", UUID.class));
    categoryEntity.setName(rs.getString("name"));
    categoryEntity.setUsername(rs.getString("username"));
    categoryEntity.setArchived(rs.getBoolean("archived"));

    spendEntity.setId(rs.getObject("id", UUID.class));
    spendEntity.setUsername(rs.getString("username"));
    spendEntity.setSpendDate(rs.getDate("spend_date"));
    spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    spendEntity.setAmount(rs.getDouble("amount"));
    spendEntity.setDescription(rs.getString("description"));
    spendEntity.setCategory(categoryEntity);

    return spendEntity;
  }
}
