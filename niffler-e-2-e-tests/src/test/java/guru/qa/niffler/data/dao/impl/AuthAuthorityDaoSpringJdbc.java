package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

  private final DataSource dataSource;

  public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void create(AuthorityEntity entity) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.update(
      con -> {
        PreparedStatement ps = con.prepareStatement(
          "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
          Statement.RETURN_GENERATED_KEYS
        );
        ps.setObject(1, entity.getUserId());
        ps.setString(2, entity.getAuthority().name());
        return ps;
      }
    );
  }

  @Override
  public void delete(AuthorityEntity entity) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
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
