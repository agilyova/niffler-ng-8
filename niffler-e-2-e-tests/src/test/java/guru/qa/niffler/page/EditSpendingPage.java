package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement submitBtn = $("#save");

  public void editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    submitBtn.click();
  }

  public MainPage editAmount(Double amount) {
    amountInput.setValue(String.valueOf(amount));
    submitBtn.click();
    return new MainPage();
  }
}
