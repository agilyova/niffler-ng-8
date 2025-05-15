package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement errorMessageElement = $(".form__error");

  public static final String URL = Config.getInstance().authUrl() + "login";

  public LoginPage fillLoginForm(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    return this;
  }

  public MainPage doLogin(String username, String password) {
    fillLoginForm(username, password);
    submitBtn.click();
    return new MainPage();
  }

  public <T> T submitForm(T returnPage) {
    submitBtn.click();
    return returnPage;
  }

  public LoginPage checkErrorMessageShown(String message) {
    errorMessageElement.shouldHave(exactText(message));
    return this;
  }
}
