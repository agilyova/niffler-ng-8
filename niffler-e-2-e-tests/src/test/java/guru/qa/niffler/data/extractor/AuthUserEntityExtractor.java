package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthUserEntityExtractor implements ResultSetExtractor<AuthUserEntity> {
  public static final AuthUserEntityExtractor instance = new AuthUserEntityExtractor();

  private AuthUserEntityExtractor() {
  }

  @Override
  public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
    Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
    UUID userId = null;
    while (rs.next()) {
      userId = rs.getObject("id", UUID.class);
      AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
        AuthUserEntity au = new AuthUserEntity();
        try {
          au.setId(id);
          au.setUsername(rs.getString("username"));
          au.setPassword(rs.getString("password"));
          au.setEnabled(rs.getBoolean("enabled"));
          au.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          au.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          au.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
        return au;
      });
      AuthorityEntity authority = new AuthorityEntity();
      authority.setId(rs.getObject("auth_id", UUID.class));
      authority.setAuthority(Authority.valueOf(rs.getString("authority")));
      user.getAuthorities().add(authority);
    }
    return userId != null ? userMap.get(userId) : null;
  }
}
