package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.utils.ScreenDiffResult;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
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
  private final SelenideElement currencyFilter = $("#currency");
  private final SelenideElement currencyList = $("ul[role='listbox']");
  private final ElementsCollection currencyListItems = $$("li[data-value]");
  private final SelenideElement diagramElement = $("canvas[role = 'img']");
  private final ElementsCollection badges = $$("#legend-container li");
  private final SelenideElement deleteButton = $("#delete");
  private final SelenideElement confirmDialog = $("div[role = 'dialog']");
  private final SelenideElement submitActionButton = confirmDialog.$("button:last-of-type");
  private final SelenideElement menuButton = $("button[aria-label= 'Menu']");
  private final ElementsCollection menuItems = $$("li[role = 'menuitem']");


  public MainPage() {
    spendingTable.shouldBe(visible);
  }

  public ProfilePage goToProfilePage() {
    menuButton.click();
    menuItems.findBy(text("Profile")).click();
    return new ProfilePage();
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

  public MainPage selectCurrency(CurrencyValues currency) {
    currencyFilter.click();
    currencyList.shouldBe(visible);
    currencyListItems.findBy(text(currency.name())).click();
    return this;
  }

  public MainPage deleteSpending(String description) {
    tableRows.find(text(description)).click();
    deleteButton.click();
    confirmDialog.should(appear);
    return this;
  }

  public MainPage approveAction() {
    submitActionButton.click();
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

  @SneakyThrows
  public MainPage checkDiagram(BufferedImage expected) {
    Selenide.sleep(3000);
    BufferedImage actual = ImageIO.read(diagramElement.screenshot());

    Assertions.assertFalse(
      new ScreenDiffResult(
        expected,
        actual
      )
    );
    return this;
  }

  public MainPage checkBadges(String... badgesTexts) {
    badges.shouldHave(textsInAnyOrder(badgesTexts));
    return this;
  }
}
