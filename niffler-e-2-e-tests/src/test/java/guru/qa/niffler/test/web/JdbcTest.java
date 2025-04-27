package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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

  @Test
  void findByIdRepo() {
    UserDbClient userDbClient = new UserDbClient();
    UserEntity ud = userDbClient.findUserByIdRepo(UUID.fromString("d88087ec-a301-4b4c-bcf7-3241a4c6a0d6"));

    System.out.println(ud);
    System.out.println("Мои запросы дружить и подтвержденная дружба");
    ud.getFriendshipRequests().forEach(System.out::println);
    System.out.println("Приглашения мне дружить");
    ud.getFriendshipAddressees().forEach(System.out::println);
  }

  @Test
  void addInvitationRepo() {
    UserDbClient userDbClient = new UserDbClient();
    UserJson requester = new UserJson(
      null,
      RandomDataUtils.randomUserName(),
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    UserJson addressee = new UserJson(
      null,
      RandomDataUtils.randomUserName(),
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    System.out.println(requester);
    System.out.println(addressee);

    UserJson createdRequester = userDbClient.createUserRepo(requester);
    UserJson createdAddressee = userDbClient.createUserRepo(addressee);

    userDbClient.addInvitationRepo(createdRequester, createdAddressee);
  }

  @Test
  void addExistedInvitationRepo() {
    UserDbClient userDbClient = new UserDbClient();
    UserJson requester = new UserJson(
      UUID.fromString("5ab3ef0a-2054-11f0-bad0-0242ac110004"),
      "rene.monahan",
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    UserJson addressee = new UserJson(
      UUID.fromString("5ac459e4-2054-11f0-bad0-0242ac110004"),
      "gilberto.rowe",
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    System.out.println(requester);
    System.out.println(addressee);

    userDbClient.addInvitationRepo(requester, addressee);
  }

  @Test
  void addFriendRepo() {
      UserDbClient userDbClient = new UserDbClient();
      UserJson requester = new UserJson(
        null,
        RandomDataUtils.randomUserName(),
        null,
        null,
        null,
        CurrencyValues.EUR,
        null,
        null
      );
      UserJson addressee = new UserJson(
        null,
        RandomDataUtils.randomUserName(),
        null,
        null,
        null,
        CurrencyValues.EUR,
        null,
        null
      );
      System.out.println(requester);
      System.out.println(addressee);
      UserJson createdRequester = userDbClient.createUserRepo(requester);
      UserJson createdAddressee = userDbClient.createUserRepo(addressee);

      userDbClient.addFriendRepo(createdRequester, createdAddressee);
  }

  @Test
  void createUserSpringRepo() {
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
    UserJson createdUser = usersDbClient.createUserSpringRepo(user);
    System.out.println(createdUser);
  }

  @Test
  void findAuthUserSpringRepo() {
    UserDbClient usersDbClient = new UserDbClient();
    AuthUserEntity userByIdSpringRepo = usersDbClient.
      findAuthUserByIdSpringRepo(UUID.fromString("7c4d1511-ba07-4b23-af8e-feaed1665d94"));
    System.out.println(userByIdSpringRepo);
  }

  @Test
  void findUserByIdSpringRepo() {
    UserDbClient userDbClient = new UserDbClient();
    UserEntity ud = userDbClient.findUserByIdSpringRepo(UUID.fromString("d88087ec-a301-4b4c-bcf7-3241a4c6a0d6"));

    System.out.println(ud);
    System.out.println("Мои запросы дружить и подтвержденная дружба");
    ud.getFriendshipRequests().forEach(System.out::println);
    System.out.println("Приглашения мне дружить");
    ud.getFriendshipAddressees().forEach(System.out::println);
  }

  @Test
  void addInvitationSpringRepo() {
    UserDbClient userDbClient = new UserDbClient();
    UserJson requester = new UserJson(
      null,
      RandomDataUtils.randomUserName(),
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    UserJson addressee = new UserJson(
      null,
      RandomDataUtils.randomUserName(),
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    System.out.println(requester);
    System.out.println(addressee);

    UserJson createdRequester = userDbClient.createUserSpringRepo(requester);
    UserJson createdAddressee = userDbClient.createUserSpringRepo(addressee);

    userDbClient.addInvitationSpringRepo(createdRequester, createdAddressee);
  }

  @Test
  void addExistedInvitationSpringRepo() {
    UserDbClient userDbClient = new UserDbClient();
    UserJson requester = new UserJson(
      UUID.fromString("5ab3ef0a-2054-11f0-bad0-0242ac110004"),
      "rene.monahan",
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    UserJson addressee = new UserJson(
      UUID.fromString("5ac459e4-2054-11f0-bad0-0242ac110004"),
      "gilberto.rowe",
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    System.out.println(requester);
    System.out.println(addressee);

    userDbClient.addInvitationSpringRepo(requester, addressee);
  }

  @Test
  void addFriendSpringRepo() {
    UserDbClient userDbClient = new UserDbClient();
    UserJson requester = new UserJson(
      null,
      RandomDataUtils.randomUserName(),
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    UserJson addressee = new UserJson(
      null,
      RandomDataUtils.randomUserName(),
      null,
      null,
      null,
      CurrencyValues.EUR,
      null,
      null
    );
    System.out.println(requester);
    System.out.println(addressee);
    UserJson createdRequester = userDbClient.createUserRepo(requester);
    UserJson createdAddressee = userDbClient.createUserRepo(addressee);

    userDbClient.addFriendSpringRepo(createdRequester, createdAddressee);
  }
}
