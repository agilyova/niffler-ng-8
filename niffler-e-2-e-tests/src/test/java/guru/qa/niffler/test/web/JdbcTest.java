package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

public class JdbcTest {
  @Test
  void shouldCreateUserSpringTx() {
    UserDbClient usersDbClient = new UserDbClient();

    UserJson user = new UserJson(
      null,
      RandomDataUtils.randomUserName(),
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    UserJson createdUser = usersDbClient.createUser(user);
    System.out.println(createdUser);
  }

  @Test
  void shouldCreateWithUserRepo() {
    UserDbClient usersDbClient = new UserDbClient();

    UserJson user = new UserJson(
      null,
      RandomDataUtils.randomUserName(),
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    UserJson createdUser = usersDbClient.createUserRepo(user);
    System.out.println(createdUser);
  }
}
