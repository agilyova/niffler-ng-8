package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.RandomUser;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJ;
import guru.qa.niffler.page.RegisterPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

  @Test
  @RandomUser
  void shouldRegisterNewUser(UserJ user) {

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .fillRegistrationForm(user.getUsername(), user.getPassword(), user.getPassword())
      .submitRegistration()
      .checkSuccessRegisterMessageShown()
      .goToLoginPage()
      .doLogin(user.getUsername(), user.getPassword())
      .checkThatSpendingTableIsEmpty();
  }

  @Test
  @RandomUser
  void shouldNotRegisterUserWithExistingUserName(UserJ user) {

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(user)
      .checkSuccessRegisterMessageShown()
      .openRegisterPage()
      .registerUser(user)
      .checkErrorRegisterMessageShown("Username `%s` already exists".formatted(user.getUsername()));
  }

  @Test
  @RandomUser
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual(UserJ user) {

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .fillRegistrationForm(
        user.getUsername(),
        user.getPassword(),
        RandomDataUtils.randomPassword(3, 12))
      .submitRegistration()
      .checkErrorRegisterMessageShown("Passwords should be equal");
  }

  @Test
  @RandomUser
  void shouldShowErrorIfUserNameLessThenThreeCharacters(UserJ user) {

    user.setUsername(RandomDataUtils.randomString(2));

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(user)
      .submitRegistration()
      .checkErrorRegisterMessageShown("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  @RandomUser
  void shouldShowErrorIfPasswordLessThenThreeCharacters(UserJ user) {

    user.setPassword(RandomDataUtils.randomPassword(1, 2));

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(user)
      .submitRegistration()
      .checkErrorRegisterMessageShown("Allowed password length should be from 3 to 12 characters");
  }
}
