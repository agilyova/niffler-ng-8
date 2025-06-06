package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage> {
  private final SelenideElement alert = $(".MuiAlert-message");

  @Step("Check that alert has message '{0}'")
  public T checkAlertMessage(String message) {
    alert.shouldHave(text(message));
    return (T) this;
  }
}
