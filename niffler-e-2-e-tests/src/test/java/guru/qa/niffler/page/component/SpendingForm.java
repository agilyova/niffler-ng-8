package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.page.BasePage;
import io.qameta.allure.Step;
import lombok.Getter;

import java.time.LocalDate;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SpendingForm<T extends BasePage> {
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement currencyFilterInput = $("#currency");
  private final SelenideElement currencyList = $("ul[role='listbox']");
  private final ElementsCollection currencyListItems = $$("li[data-value]");
  private final SelenideElement categoryInput = $("#category");
  private final ElementsCollection categoryItems = $$("#category~ul>li");
  private final SelenideElement dateInput = $("input[name = 'date']");
  private final SelenideElement calendarButton = $("input[name = 'date']~div");
  private final SelenideElement descriptionInput = $("#description");
  private final Calendar calendar = new Calendar();
  @Getter
  private final T parentPage;

  public SpendingForm(T parentPage) {
    this.parentPage = parentPage;
  }

  @Step("Fill Amount = {0}")
  public SpendingForm<T> setAmount(double amount) {
    amountInput.setValue(String.valueOf(amount));
    return this;
  }

  @Step("Select currency = {0}")
  public SpendingForm<T> selectCurrency(CurrencyValues currency) {
    currencyFilterInput.click();
    currencyList.shouldBe(visible);
    currencyListItems.findBy(text(currency.name())).click();
    return this;
  }

  @Step("Select category = {0}")
  public SpendingForm<T> selectCategory(String categoryName) {
    categoryItems.findBy(text(categoryName)).click();
    return this;
  }

  @Step("Add new category {0}")
  public SpendingForm<T> createCategory(String categoryName) {
    categoryInput.setValue(categoryName).pressEnter();
    return this;
  }

  @Step("Select date {0}")
  public SpendingForm<T> selectDate(LocalDate date) {
    calendarButton.click();
    calendar.selectDateInCalendar(date);
    return this;
  }

  @Step("Fill Description = {0}")
  public SpendingForm<T> setDescription(String description) {
    descriptionInput.setValue(description);
    return this;
  }
}
