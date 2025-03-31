package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

  public static final String URL = Config.getInstance().authUrl() + "register";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement loginBtn = $(".form_sign-in");
  private final SelenideElement successMessageElement = $(".form__paragraph_success");
  private final SelenideElement errorMessageElement = $(".form__error");

  public RegisterPage fillRegistrationForm(String username, String password, String confirmPassword) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    passwordSubmitInput.setValue(confirmPassword);
    return this;
  }
  public RegisterPage submitRegistration() {
    submitBtn.click();
    return this;
  }

  public LoginPage goToLoginPage() {
    loginBtn.click();
    return new LoginPage();
  }

  public RegisterPage registerUser(UserJson user) {
    fillRegistrationForm(user.getUsername(), user.getPassword(), user.getPassword())
      .submitRegistration();
    return this;
  }

  public RegisterPage openRegisterPage() {
    return Selenide.open(RegisterPage.URL, RegisterPage.class);
  }


  public RegisterPage checkSuccessRegisterMessageShown() {
    successMessageElement.shouldBe(Condition.visible);
    return this;
  }

  public RegisterPage checkErrorRegisterMessageShown(String message) {
    errorMessageElement.shouldHave(exactText(message));
    return this;
  }
}
