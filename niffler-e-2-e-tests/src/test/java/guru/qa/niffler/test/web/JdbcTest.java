package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class JdbcTest {
  @Test
  void userCreate() {
    UsersClient uc = new UsersDbClient();
    uc.createUser("sashaD", "123456");
  }

  @Test
  void addIncomeInvitation() {
    UsersClient uc = new UsersDbClient();
    UserJson requester = new UserJson(
      UUID.fromString("40f75fdc-5c36-47d2-88d3-868a791003ef"),
      "sashaD",
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    uc.createIncomeInvitations(requester, 1);
  }

  @Test
  void addOutComeInvitation() {
    UsersClient uc = new UsersDbClient();
    UserJson requester = new UserJson(
      null,
      "sashaD",
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    uc.createOutcomeInvitations(requester, 2);
  }

  @Test
  void addFriends() {
    UsersClient uc = new UsersDbClient();
    UserJson requester = new UserJson(
      UUID.fromString("d6c3a2c9-52ff-4dfd-9fa2-1129e50d9fab"),
      "sashaD",
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    uc.createFriends(requester, 1);
  }

  @Test
  void totalRemoveUser() {
    UsersClient uc = new UsersDbClient();
    UserJson user = new UserJson(
      UUID.fromString("f838b7d4-2517-11f0-b4ee-0242ac110004"),
      "sashaD",
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    uc.remove(user);
  }

  @Test
  void createSpend() {
    SpendDbClient spendDbClient = new SpendDbClient();
    SpendJson spendJson = new SpendJson(
      null,
      new Date(),
      new CategoryJson(
        null,
        "test-jdbc2",
        "friend",
        false
      ),
      CurrencyValues.RUB,
      4.2,
      "Test-spring",
      "friend"
    );
    spendDbClient.create(spendJson);
  }

  @Test
  void deleteSpend() {
    SpendDbClient spendDbClient = new SpendDbClient();
    SpendJson spendJson = new SpendJson(
      null,
      new Date(),
      new CategoryJson(
        null,
        "test-jdbc3",
        "friend",
        false
      ),
      CurrencyValues.RUB,
      4.2,
      RandomDataUtils.randomCategoryName(),
      "friend"
    );
    SpendJson createdSpend = spendDbClient.create(spendJson);
    spendDbClient.remove(createdSpend);
  }
}
