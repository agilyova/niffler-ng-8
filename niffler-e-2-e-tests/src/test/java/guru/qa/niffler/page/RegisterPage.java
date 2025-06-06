package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage> {

  public static final String URL = Config.getInstance().authUrl() + "register";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement loginBtn = $(".form_sign-in");
  private final SelenideElement successMessageElement = $(".form__paragraph_success");
  private final SelenideElement errorMessageElement = $(".form__error");

  @Step("Fill registration form username={0} password={2} confirmPassword={2} ")
  public RegisterPage fillRegistrationForm(String username, String password, String confirmPassword) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    passwordSubmitInput.setValue(confirmPassword);
    return this;
  }

  @Step("Submit registration")
  public RegisterPage submitRegistration() {
    submitBtn.click();
    return this;
  }

  @Step("Go to Login Page")
  public LoginPage goToLoginPage() {
    loginBtn.click();
    return new LoginPage();
  }

  @Step("Register user {0}/{1}")
  public RegisterPage registerUser(String username, String password) {
    fillRegistrationForm(username, password, password)
      .submitRegistration();
    return this;
  }

  public RegisterPage openRegisterPage() {
    return Selenide.open(RegisterPage.URL, RegisterPage.class);
  }

  @Step("Check that success register message is present")
  public RegisterPage checkSuccessRegisterMessageShown() {
    successMessageElement.shouldBe(Condition.visible);
    return this;
  }

  @Step("Check that error message \"{0}\" is present")
  public RegisterPage checkErrorRegisterMessageShown(String message) {
    errorMessageElement.shouldHave(exactText(message));
    return this;
  }
}
