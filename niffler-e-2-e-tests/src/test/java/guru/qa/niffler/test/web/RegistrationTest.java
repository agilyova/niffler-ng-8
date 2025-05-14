package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.RegisterPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

  @Test
  void shouldRegisterNewUser() {
    String username = RandomDataUtils.randomUserName();
    String password = RandomDataUtils.randomPassword(3, 12);

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .fillRegistrationForm(username, password, password)
      .submitRegistration()
      .checkSuccessRegisterMessageShown()
      .goToLoginPage()
      .doLogin(username, password)
      .checkThatSpendingTableIsEmpty();
  }

  @Test
  @User
  void shouldNotRegisterUserWithExistingUserName(UserJson user) {

    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(user.username(), user.testData().password())
      .checkErrorRegisterMessageShown("Username `%s` already exists".formatted(user.username()));
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .fillRegistrationForm(
        RandomDataUtils.randomUserName(),
        RandomDataUtils.randomPassword(3, 12),
        RandomDataUtils.randomPassword(3, 12))
      .submitRegistration()
      .checkErrorRegisterMessageShown("Passwords should be equal");
  }

  @Test
  void shouldShowErrorIfUserNameLessThenThreeCharacters() {
    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(RandomDataUtils.randomString(2), RandomDataUtils.randomPassword(3, 12))
      .submitRegistration()
      .checkErrorRegisterMessageShown("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  void shouldShowErrorIfPasswordLessThenThreeCharacters() {
    Selenide.open(RegisterPage.URL, RegisterPage.class)
      .registerUser(RandomDataUtils.randomUserName(), RandomDataUtils.randomPassword(2, 3))
      .submitRegistration()
      .checkErrorRegisterMessageShown("Allowed password length should be from 3 to 12 characters");
  }
}
