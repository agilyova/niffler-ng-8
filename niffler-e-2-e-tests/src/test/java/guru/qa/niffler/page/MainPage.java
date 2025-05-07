package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement noSpendingTitle = $(byText("There are no spendings"));
  private final SelenideElement spendingTable = $("#spendings");
  private final SelenideElement searchInputElement = $("input[aria-label='search']");
  private final SelenideElement spinnerElement = $(".MuiCircularProgress-root");

  public MainPage() {
    spendingTable.shouldBe(visible);
  }

  public EditSpendingPage editSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public MainPage searchForSpending(String description) {
    searchInputElement.setValue(description).pressEnter();
    spinnerElement.should(disappear);
    return this;
  }

  public void checkThatTableContains(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .should(visible);
  }

  public MainPage checkThatSpendingTableIsEmpty() {
    noSpendingTitle.shouldBe(visible);
    tableRows.shouldHave(size(0));
    return this;
  }

}
