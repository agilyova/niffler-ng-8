package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public void create(AuthorityEntity entity) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    jdbcTemplate.update(
      con -> {
        PreparedStatement ps = con.prepareStatement(
          "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
          Statement.RETURN_GENERATED_KEYS
        );
        ps.setObject(1, entity.getUser().getId());
        ps.setString(2, entity.getAuthority().name());
        return ps;
      }
    );
  }

  @Override
  public void create(AuthorityEntity... authority) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    jdbcTemplate.batchUpdate(
      "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
      new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ps.setObject(1, authority[i].getUser().getId());
          ps.setString(2, authority[i].getAuthority().name());
        }

        @Override
        public int getBatchSize() {
          return authority.length;
        }
      }
    );
  }

  @Override
  public List<AuthorityEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    return jdbcTemplate.query(
      "SELECT * FROM authority",
      AuthorityEntityRowMapper.instance
    );
  }

  @Override
  public void delete(AuthorityEntity entity) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    jdbcTemplate.update(
      con -> {
        PreparedStatement ps = con.prepareStatement(
          "DELETE FROM authority WHERE id = ?"
        );
        ps.setObject(1, entity.getId());
        return ps;
      }
    );
  }
}
