package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement errorMessageElement = $(".form__error");

  public static final String URL = Config.getInstance().authUrl() + "login";

  @Step("Fill login form {0} / {1}")
  public LoginPage fillLoginForm(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    return this;
  }

  @Step("Login as {0} / {1}")
  public MainPage doLogin(String username, String password) {
    fillLoginForm(username, password);
    submitBtn.click();
    return new MainPage();
  }

  @Step("Submit login")
  public <T> T submitForm(T returnPage) {
    submitBtn.click();
    return returnPage;
  }

  @Step("Check that error \"{0}\" is shown")
  public LoginPage checkErrorMessageShown(String message) {
    errorMessageElement.shouldHave(exactText(message));
    return this;
  }
}
