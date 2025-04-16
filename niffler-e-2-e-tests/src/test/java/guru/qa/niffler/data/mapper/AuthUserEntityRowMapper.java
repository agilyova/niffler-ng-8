package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class  AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

  public static final AuthUserEntityRowMapper instance = new AuthUserEntityRowMapper();

  private AuthUserEntityRowMapper() {}

  @Override
  public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    AuthUserEntity au = new AuthUserEntity();
    au.setId(rs.getObject("id", UUID.class));
    au.setUsername(rs.getString("username"));
    au.setPassword(rs.getString("password"));
    au.setEnabled(rs.getBoolean("enabled"));
    au.setAccountNonExpired(rs.getBoolean("account_non_expired"));
    au.setAccountNonLocked(rs.getBoolean("account_non_locked"));
    au.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    return au;
  }
}
