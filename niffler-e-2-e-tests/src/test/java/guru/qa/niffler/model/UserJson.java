package guru.qa.niffler.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserJson {
  String username;
  String password;
}