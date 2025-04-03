package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.RandomUser;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

  @Test
  @RandomUser
  void shouldRegisterNewUser(UserJson user) {

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
  void shouldNotRegisterUserWithExistingUserName(UserJson user) {

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(user)
      .checkSuccessRegisterMessageShown()
      .openRegisterPage()
      .registerUser(user)
      .checkErrorRegisterMessageShown("Username `%s` already exists".formatted(user.getUsername()));
  }

  @Test
  @RandomUser
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual(UserJson user) {

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .fillRegistrationForm(
        user.getUsername(),
        user.getPassword(),
        new Faker().internet().password())
      .submitRegistration()
      .checkErrorRegisterMessageShown("Passwords should be equal");
  }

  @Test
  @RandomUser
  void shouldShowErrorIfUserNameLessThenThreeCharacters(UserJson user) {

    user.setUsername(new Faker().lorem().characters(1,2));

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(user)
      .submitRegistration()
      .checkErrorRegisterMessageShown("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  @RandomUser
  void shouldShowErrorIfPasswordLessThenThreeCharacters(UserJson user) {

    user.setPassword(new Faker().internet().password(1,2));

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(user)
      .submitRegistration()
      .checkErrorRegisterMessageShown("Allowed password length should be from 3 to 12 characters");
  }
}
