package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

public class UserDbClientTest {

  @Test
  void userTest() {
    UserJson user = new UserJson(
      null,
      "user-test-v4",
      "Aleksandra",
      null,
      null,
      CurrencyValues.RUB,
      null,
      null
    );

    UserDbClient userDbClient = new UserDbClient();
    userDbClient.createUser(user, "123");
  }}
