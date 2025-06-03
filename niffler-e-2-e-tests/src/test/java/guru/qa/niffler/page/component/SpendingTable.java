package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendsConditions;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SpendingTable {

  private final SelenideElement self = $("#spendings");
  private final SelenideElement noSpendingTitle = $(byText("There are no spendings"));
  private final ElementsCollection tableRows = self.$$("table>tbody tr");
  private final SelenideElement deleteButton = $("#delete");
  private final SelenideElement confirmDialogEl = $("div[role = 'dialog']");
  private final SelenideElement submitActionButton = confirmDialogEl.$("button:last-of-type");
  private final SelenideElement searchInputElement = $("input[aria-label='search']");
  private final SelenideElement spinnerElement = $(".MuiCircularProgress-root");
  private final SelenideElement currencyFilter = $("#currency");
  private final SelenideElement currencyList = $("ul[role='listbox']");
  private final ElementsCollection currencyListItems = $$("li[data-value]");
  private final SearchField searchField = new SearchField();

  @Step("Edit spending {0}")
  public EditSpendingPage editSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription))
      .$$("td")
      .get(5)
      .click();
    return new EditSpendingPage();
  }

  @Step("Delete spending {0}")
  public SpendingTable deleteSpending(String description) {
    tableRows.find(text(description)).click();
    deleteButton.click();
    confirmDialogEl.should(appear);
    return this;
  }

  @Step("Search spending by description \"{0}\"")
  public SpendingTable searchForSpending(String description) {
    searchField.search(description);
    spinnerElement.should(disappear);
    return this;
  }

  @Step("Select currency {0}")
  public SpendingTable selectCurrency(CurrencyValues currency) {
    currencyFilter.click();
    currencyList.shouldBe(visible);
    currencyListItems.findBy(text(currency.name())).click();
    return this;
  }

  @Step("Approve action")
  public MainPage approveAction() {
    submitActionButton.click();
    return new MainPage();
  }

  @Step("Check that spending table is empty")
  public SpendingTable checkThatSpendingTableIsEmpty() {
    noSpendingTitle.shouldBe(visible);
    tableRows.shouldHave(size(0));
    return this;
  }

  @Step("Check that spending table has exact values")
  public SpendingTable checkTableHaveExactSpends(SpendJson... spendings) {
    tableRows.shouldHave(SpendsConditions.spends(spendings));
    return this;
  }

  @Step("Check that spending table contains values")
  public SpendingTable checkThatTableContains(String spendingDescription) {
    tableRows.find(text(spendingDescription))
      .should(visible);
    return this;
  }

}
