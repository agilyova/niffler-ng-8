package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.RandomUser;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJ;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {

  @Test
  @User
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .fillLoginForm(user.username(), RandomDataUtils.randomPassword(3, 12))
      .submitForm(new LoginPage())
      .checkErrorMessageShown("Неверные учетные данные пользователя");
  }
}
