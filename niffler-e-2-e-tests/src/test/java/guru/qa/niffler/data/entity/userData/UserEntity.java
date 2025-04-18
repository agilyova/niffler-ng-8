package guru.qa.niffler.data.entity.userData;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {

  private UUID id;
  private String username;
  private CurrencyValues currency;
  private String firstname;
  private String surname;
  private String fullname;
  private byte[] photo;
  private byte[] photoSmall;

  public static UserEntity fromJson(UserJson user) {
    UserEntity ue = new UserEntity();
    ue.setId(user.id());
    ue.setUsername(user.username());
    ue.setCurrency(user.currency());
    ue.setFirstname(user.firstname());
    ue.setSurname(user.surname());
    ue.setFullname(user.fullname());
    ue.setPhoto(user.photo() != null ? user.photo().getBytes() : null);
    ue.setPhotoSmall(user.photoSmall() != null ? user.photoSmall().getBytes() : null);
    return ue;
  }
}