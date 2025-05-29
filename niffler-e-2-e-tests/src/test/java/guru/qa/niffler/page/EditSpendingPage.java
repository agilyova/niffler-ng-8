package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SpendingForm;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage extends BasePage {

  private final SelenideElement saveChangesButton = $("#save");
  @Getter
  private final SpendingForm<EditSpendingPage> spendingForm = new SpendingForm<>(this);

  @Step("Сохранить изменения")
  public MainPage saveChanges() {
    saveChangesButton.click();
    return new MainPage();
  }
}
