package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public CategoryEntity create(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(
      con -> {
        PreparedStatement ps = con.prepareStatement(
          "INSERT INTO category (name, username, archived) " +
            "VALUES (?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, category.getName());
        ps.setString(2, category.getUsername());
        ps.setObject(3, category.isArchived());
        return ps;
      },
      kh
    );
    category.setId((UUID) kh.getKeys().get("id"));
    return category;
  }

  @Override
  public Optional<CategoryEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    try {
      return Optional.ofNullable(
        jdbcTemplate.queryForObject(
          "SELECT * FROM category WHERE id = ? ",
          CategoryEntityRowMapper.instance,
          id
        )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<CategoryEntity> findByUsernameAndCategoryName(String username, String categoryName) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    return Optional.ofNullable(
      jdbcTemplate.queryForObject(
        "SELECT * FROM category WHERE username = ? AND name = ?",
        CategoryEntityRowMapper.instance,
        username,
        categoryName
      )
    );
  }

  @Override
  public List<CategoryEntity> findAllByUserName(String userName) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    return jdbcTemplate.query(
      "SELECT * FROM category WHERE username = ?",
      CategoryEntityRowMapper.instance,
      userName
    );
  }

  @Override
  public List<CategoryEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    return jdbcTemplate.query(
      "SELECT * FROM category",
      CategoryEntityRowMapper.instance
    );
  }

  @Override
  public void delete(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    jdbcTemplate.update(
      "DELETE FROM category WHERE id = ?",
      category.getId()
    );
  }
}
