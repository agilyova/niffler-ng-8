package guru.qa.niffler.data.entity.userAuth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
public class AuthUserEntity implements Serializable {
  private UUID id;
  private String username;
  private String password;
  private Boolean enabled = true;
  private Boolean accountNonExpired = true;
  private Boolean accountNonLocked = true;
  private Boolean credentialsNonExpired = true;
  private List<AuthorityEntity> authorities = new ArrayList<>();
}
