package guru.qa.niffler.test.web.temp;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

@WebTest
public class OAuthTest {

  private static final Config CFG = Config.getInstance();

  @Test
  @ApiLogin(username = "friend", password = "12345")
  void oauthTest(UserJson user) throws InterruptedException {
    Selenide.open(ProfilePage.URL);
    System.out.println(user);
  }
}