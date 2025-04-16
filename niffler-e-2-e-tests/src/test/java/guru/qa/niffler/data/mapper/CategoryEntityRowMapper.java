package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

  public final static CategoryEntityRowMapper instance = new CategoryEntityRowMapper();

  private CategoryEntityRowMapper() {
  }

  @Override
  public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    CategoryEntity ce = new CategoryEntity();
    ce.setId(rs.getObject("id", UUID.class));
    ce.setName(rs.getString("name"));
    ce.setUsername(rs.getString("username"));
    ce.setArchived(rs.getBoolean("archived"));
    return ce;
  }
}
