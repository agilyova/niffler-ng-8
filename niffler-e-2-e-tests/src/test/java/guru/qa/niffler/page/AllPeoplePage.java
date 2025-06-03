package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage {

  public static final String URL = Config.getInstance().frontUrl() + "people/all";

  private final SelenideElement peopleTab = $("div[aria-label = 'People tabs']");
  private final SelenideElement spinnerElement = $(".MuiCircularProgress-root");
  private final SelenideElement allPeopleTable = $("#all");
  private final SelenideElement searchInputElement = $("input[aria-label='search']");
  private final ElementsCollection personRows = allPeopleTable.$$("tr");

  public AllPeoplePage() {
    peopleTab.shouldBe(visible);
    spinnerElement.should(disappear);
  }

  @Step("Search person by string {0}")
  public AllPeoplePage searchForPerson(String username) {
    searchInputElement.setValue(username).pressEnter();
    spinnerElement.should(disappear);
    return this;
  }

  @Step("Check that outcome invitation to {0} is present in All people table")
  public AllPeoplePage checkOutcomeInvitationPresentInAllPeopleTable(String userName) {
    findPerson(userName).$(byText("Waiting...")).shouldBe(visible);
    return this;
  }

  private SelenideElement findPerson(String username) {
    return personRows.find(text(username));
  }
}
