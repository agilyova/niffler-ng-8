package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.RandomUser;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {

  @Test
  @RandomUser
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserJson user) {
    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(user)
      .checkSuccessRegisterMessageShown();

    Selenide.open(LoginPage.URL, LoginPage.class)
      .fillLoginForm(user.getUsername(), new Faker().internet().password(3,12))
      .submitForm(new LoginPage())
      .checkErrorMessageShown("Неверные учетные данные пользователя");
  }
}
