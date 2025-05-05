package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import okhttp3.OkHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomUserName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient {
  private static final Config CFG = Config.getInstance();
  public static final String DEFAULT_PASSWORD = "12345";

  private final OkHttpClient client = new OkHttpClient.Builder().build();
  private final Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(CFG.userdataUrl())
    .client(client)
    .addConverterFactory(JacksonConverterFactory.create())
    .build();

  private final UserdataApi userdataApi = retrofit.create(UserdataApi.class);

  @Override
  public UserJson createUser(String username, String password) {
    throw new UnsupportedOperationException("Method not implemented yet");
  }

  @Override
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
        assertEquals(HttpStatus.SC_CREATED, response.code());
        targetUser.testData().incomeRequests().add(requester);
      }
    }
  }

  @Override
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
        assertEquals(HttpStatus.SC_CREATED, response.code());
        targetUser.testData().outcomeRequests().add(addressee);
      }
    }
  }

  @Override
  public void createFriends(UserJson targetUser, int count) {
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        UserJson requester = createUser(randomUserName(), DEFAULT_PASSWORD);
        Response response;
        try {
          userdataApi.sendInvitation(requester.username(), targetUser.username()).execute();
          response = userdataApi.acceptInvitation(requester.username(), targetUser.username()).execute();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        targetUser.testData().friends().add(requester);
      }
    }
  }

  @Override
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
  public void remove(UserJson user) {
    throw new UnsupportedOperationException("Method not implemented yet");
  }
}
