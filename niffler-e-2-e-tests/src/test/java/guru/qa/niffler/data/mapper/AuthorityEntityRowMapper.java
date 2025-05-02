package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

  public static final AuthorityEntityRowMapper instance = new AuthorityEntityRowMapper();

  private AuthorityEntityRowMapper() {
  }

  //Todo AuthUserEntity заполнять из SQL
  @Override
  public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    AuthorityEntity ae = new AuthorityEntity();
    AuthUserEntity aue = new AuthUserEntity();
    aue.setId(rs.getObject("user_id", UUID.class));

    ae.setId(rs.getObject("id", UUID.class));
    ae.setUser(aue);
    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
    return ae;
  }
}
