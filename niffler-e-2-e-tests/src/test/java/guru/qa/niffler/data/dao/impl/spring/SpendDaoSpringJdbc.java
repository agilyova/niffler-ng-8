package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public SpendEntity create(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(
      con -> {
        PreparedStatement ps = con.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, spend.getUsername());
        ps.setDate(2, new Date(spend.getSpendDate().getTime()));
        ps.setString(3, spend.getCurrency().name());
        ps.setDouble(4, spend.getAmount());
        ps.setString(5, spend.getDescription());
        ps.setObject(6, spend.getCategory().getId());
        return ps;
      },
      kh
    );
    spend.setId((UUID) kh.getKeys().get("id"));
    return spend;
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    try {
      return Optional.ofNullable(jdbcTemplate.queryForObject(
        "SELECT * FROM spend WHERE id = ?",
        SpendEntityRowMapper.instance,
        id
      ));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    List<SpendEntity> result = jdbcTemplate.query(
      "SELECT * FROM spend WHERE username = ?",
      SpendEntityRowMapper.instance,
      username
    );
    return result;
  }

  @Nonnull
  @Override
  public List<SpendEntity> findByCategory(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    List<SpendEntity> result = jdbcTemplate.query(
      "SELECT * FROM spend WHERE category_id = ?",
      SpendEntityRowMapper.instance,
      category.getId()
    );
    return result;
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    return jdbcTemplate.query(
      "SELECT * FROM spend",
      SpendEntityRowMapper.instance
    );
  }

  @Override
  public void delete(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    jdbcTemplate.update(
      "DELETE FROM spend WHERE id = ?",
      spend.getId()
    );
  }
}
