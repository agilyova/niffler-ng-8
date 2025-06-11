package guru.qa.niffler.test.web.temp;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OAuthTest {

  @Test
  @User
  void oauthTest(UserJson user) {
    AuthApiClient authApiClient = new AuthApiClient();
    String token = authApiClient.loginAs(user.username(), user.testData().password());

    Assertions.assertNotNull(token);
  }
}