package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.spring.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendWithCategoryEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

  private static final Config CFG = Config.getInstance();

  CategoryDao categoryDao = new CategoryDaoSpringJdbc();
  SpendDao spendDao = new SpendDaoSpringJdbc();

  @Override
  public SpendEntity create(SpendEntity spend) {
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
  public SpendEntity update(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    jdbcTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
          "UPDATE spend " +
            "SET username = ?," +
            "spend_date = ?," +
            "currency = ?," +
            "amount = ?," +
            "description = ?," +
            "category_id = ?" +
            "WHERE id = ?");

        ps.setString(1, spend.getUsername());
        ps.setDate(2, new Date(spend.getSpendDate().getTime()));
        ps.setString(3, spend.getCurrency().name());
        ps.setDouble(4, spend.getAmount());
        ps.setString(5, spend.getDescription());
        ps.setObject(6, spend.getCategory().getId());
        ps.setObject(7, spend.getId());
        return ps;
      }
    );
    return spend;
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
  public Optional<SpendEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

    try {
      return Optional.ofNullable(
        jdbcTemplate.queryForObject(
          "SELECT s.*," +
            "c.id AS category_id, " +
            "c.name," +
            "c.username," +
            "c.archived" +
            " FROM spend s " +
            "JOIN category c ON s.category_id = c.id " +
            "WHERE s.id = ?",
          SpendWithCategoryEntityRowMapper.instance,
          id
        )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

    try {
      return Optional.ofNullable(
        jdbcTemplate.queryForObject(
          "SELECT s.*," +
            "c.id AS category_id, " +
            "c.name," +
            "c.username," +
            "c.archived" +
            " FROM spend s " +
            "JOIN category c ON s.category_id = c.id " +
            "WHERE s.username = ? AND s.description = ?",
          SpendWithCategoryEntityRowMapper.instance,
          username, description
        )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void remove(SpendEntity spend) {
    spendDao.delete(spend);
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    jdbcTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
          "WITH deleted_spend AS (" +
            "DELETE FROM spend WHERE category_id = ?)" +
            "DELETE FROM category WHERE id = ?"
        );
        ps.setObject(1, category.getId());
        ps.setObject(2, category.getId());
        return ps;
      }
    );
  }
}
