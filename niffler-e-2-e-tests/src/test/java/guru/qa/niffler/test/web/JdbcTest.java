package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
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

    Assertions.assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
    Assertions.assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
    Assertions.assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
  }

  @Test
  void shouldCreateUserSpringWithOutTx() {
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
    UserJson createdUser = usersDbClient.createUserSpringWithOutTransactions(user);
    System.out.println(createdUser);

    Assertions.assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
    Assertions.assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
    Assertions.assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
  }

  @Test
  void shouldCreateUserJdbcWithTx() {
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
    UserJson createdUser = usersDbClient.createUserJdbcWithXaTransactions(user);
    System.out.println(createdUser);

    Assertions.assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
    Assertions.assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
    Assertions.assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
  }

  @Test
  void shouldCreateUserJdbcWithOutTx() {
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
    UserJson createdUser = usersDbClient.createUserJdbcWithOutTransactions(user);
    System.out.println(createdUser);

    Assertions.assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
    Assertions.assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
    Assertions.assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
  }

  @Test
  void shouldCreateUserWithSpringChainedTxManager() {
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
    UserJson createdUser = usersDbClient.createUserChainedTxManager(user);
    System.out.println(createdUser);

    Assertions.assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
    Assertions.assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
    Assertions.assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
  }

/*
  На некорректных данных ChainedTransactionManager отрабатывает хорошо
  и роллбеки происходят во всех транзакциях.
  Интереснее выходит, когда ошибка происходит во время коммита.

  Так как коммит в ChainedTransactionManager выполняется в обратном порядке,
  то нужно добиться ошибки в niffler-auth и проверить откатится ли коммит в niffler-userdata
  niffler-auth → insert
  niffler-userdata → insert
  niffler-userdata → commit
  niffler-auth → commit

  Как получилось воспроизвести:
  1. Установить брейкпоинт в TransactionTemplate.class на commit
  2. Запустить тест в дебаге
  3. Выполнить запрос
      SELECT pg_terminate_backend(pg_stat_activity.pid)
      FROM pg_stat_activity
      WHERE datname = 'niffler-auth'
      AND pid <> pg_backend_pid();
  4. Закончить выполнения теста
  В таком случае получится не консистентность данных:
  в БД niffler-userdata инсерт пройдет и ролбека не будет
  в БД niffler-auth данных не будет
  */
  @Test
  void shouldRollBackCreateUserWithSpringChainedTxManager() {

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

    System.out.println(user);
    usersDbClient.createUserChainedTxManager(user);
  }
}
