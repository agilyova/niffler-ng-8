package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendEntity create(SpendEntity spend) {
    try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)" +
          "VALUES (?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setString(1, spend.getUsername());
        ps.setDate(2, new Date(spend.getSpendDate().getTime()));
        ps.setString(3, spend.getCurrency().name());
        ps.setDouble(4, spend.getAmount());
        ps.setString(5, spend.getDescription());
        ps.setObject(6, spend.getCategory().getId());

        ps.executeUpdate();

        final UUID generatedKey;
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            generatedKey = rs.getObject("id", UUID.class);
          } else {
            throw new SQLException("Can't find id in ResultSet");
          }
        }
        spend.setId(generatedKey);
        return spend;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Optional<SpendEntity> findSpendById(UUID id) {
    try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM spend WHERE id = ?"
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
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM spend WHERE username = ?"
      )) {
        ps.setString(1, username);
        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          List<SpendEntity> spendList = new ArrayList<>();
          while (rs.next()) {
            SpendEntity spendEntity = fillSpendEntity(rs);
            spendList.add(spendEntity);
          }
          return spendList;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<SpendEntity> findSpendsByCategory(CategoryEntity category) {
    try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM spend WHERE category_id = ?"
      )) {
        ps.setObject(1, category.getId());
        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          List<SpendEntity> spendList = new ArrayList<>();
          while (rs.next()) {
            SpendEntity spendEntity = fillSpendEntity(rs);
            spendList.add(spendEntity);
          }
          return spendList;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM spend WHERE id = ?"
      )) {
        ps.setObject(1, spend.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static SpendEntity fillSpendEntity(ResultSet rs) throws SQLException {
    SpendEntity spendEntity = new SpendEntity();
    spendEntity.setId(rs.getObject("id", UUID.class));
    spendEntity.setUsername(rs.getString("username"));
    spendEntity.setSpendDate(rs.getDate("spend_date"));
    spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    spendEntity.setAmount(rs.getDouble("amount"));
    spendEntity.setDescription(rs.getString("description"));

    Optional<CategoryEntity> categoryEntity = new CategoryDaoJdbc()
      .findCategoryById(rs.getObject("category_id", UUID.class));
    if (categoryEntity.isPresent()) {
      spendEntity.setCategory(categoryEntity.get());
    }
    return spendEntity;
  }
}
