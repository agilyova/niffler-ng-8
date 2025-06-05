package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SpendingForm;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

public class AddSpendingPage extends BasePage<AddSpendingPage> {
  private final SelenideElement addButton = $("#save");
  private final SelenideElement cancelButton = $("#cancel");
  @Getter
  private final SpendingForm<AddSpendingPage> spendingForm = new SpendingForm<>(this);

  @Step("Submit adding new spending")
  public MainPage submitSpendingCreation() {
    addButton.click();
    return new MainPage();
  }

  @Step("Cancel adding new spending")
  public MainPage cancelSpendingCreation() {
    addButton.click();
    return new MainPage();
  }
}
