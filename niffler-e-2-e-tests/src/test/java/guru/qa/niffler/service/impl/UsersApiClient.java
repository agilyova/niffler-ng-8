package guru.qa.niffler.service.impl;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import jakarta.persistence.EntityNotFoundException;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.utils.RandomDataUtils.randomUserName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  public static final String DEFAULT_PASSWORD = "12345";

  private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
  private final UserdataApi userdataApi = new RestClient.EmtyRestClient(CFG.userdataUrl()).create(UserdataApi.class);

  @Override
  @Step("Create user '{0}' / '{1}' with API")
  public UserJson createUser(String username, String password) {
    try {
      authApi.getRegisterPage().execute();
      authApi.registerUser(
        username,
        password,
        password,
        ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
      ).execute();

      Stopwatch sw = Stopwatch.createStarted();
      long maxWaitTime = 5000;

      while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
        UserJson userJson = findUserByUsername(username);
        if (userJson != null && userJson.id() != null) {
          userJson.addTestData(
            new TestData(password)
          );
          return userJson;
        } else {
          Thread.sleep(100);
        }
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    throw new EntityNotFoundException("Created user not found");
  }

  @Override
  @Step("Create {1} income invitation for user {0.username} with API")
  public void createIncomeInvitations(UserJson targetUser, int count) {
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        UserJson requester = createUser(randomUserName(), DEFAULT_PASSWORD);
        Response response;
        try {
          response = userdataApi.sendInvitation(requester.username(), targetUser.username()).execute();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        targetUser.testData().incomeRequests().add(requester);
      }
    }
  }

  @Override
  @Step("Create {1} outcome invitation from user {0.username} with API")
  public void createOutcomeInvitations(UserJson targetUser, int count) {
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        UserJson addressee = createUser(randomUserName(), DEFAULT_PASSWORD);
        Response response;
        try {
          response = userdataApi.sendInvitation(targetUser.username(), addressee.username()).execute();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        targetUser.testData().outcomeRequests().add(addressee);
      }
    }
  }

  @Override
  @Step("Create {1} friends for user {0.username} with API")
  public void createFriends(UserJson targetUser, int count) {
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        UserJson requester = createUser(randomUserName(), DEFAULT_PASSWORD);
        Response response;
        try {
          userdataApi.sendInvitation(requester.username(), targetUser.username()).execute();
          response = userdataApi.acceptInvitation(targetUser.username(), requester.username()).execute();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        targetUser.testData().friends().add(requester);
      }
    }
  }

  @Override
  @Step("Find user by username \"{0}\" with API")
  public UserJson findUserByUsername(String userName) {
    final Response<UserJson> response;
    try {
      response = userdataApi.getUser(userName).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }

  @Override
  @Step("Delete user \"{0.username}\" with API")
  public void remove(UserJson user) {
    throw new UnsupportedOperationException("Method not implemented yet");
  }
}
