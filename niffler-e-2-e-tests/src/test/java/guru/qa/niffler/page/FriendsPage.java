package guru.qa.niffler.page;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage<FriendsPage> {

  public static final String URL = Config.getInstance().frontUrl() + "people/friends";

  private final SelenideElement peopleTab = $("div[aria-label = 'People tabs']");
  private final SelenideElement spinnerElement = $(".MuiCircularProgress-root");
  private final SelenideElement noFriendsTextElement = $(byText("There are no users yet"));
  private final SelenideElement friendsRequestTable = $("table:has(#requests)");
  private final SelenideElement friendsTable = $("table:has(#friends)");
  private final SelenideElement popup = $("div[role = 'dialog']");
  private final SelenideElement submitActionBtn = popup.$(".MuiDialogActions-root>button:last-of-type");

  public FriendsPage() {
    peopleTab.shouldBe(visible);
    spinnerElement.should(disappear);
  }

  @Step("Accept income invitation from {0}")
  public FriendsPage acceptIncomeInvitation(String from) {
    SelenideElement incomeInvitation = findTableRow(friendsRequestTable, from);
    incomeInvitation.$("button:first-of-type").shouldHave(text("Accept")).click();
    return this;
  }

  @Step("Decline income invitation from {0}")
  public FriendsPage declineIncomeInvitation(String from) {
    SelenideElement incomeInvitation = findTableRow(friendsRequestTable, from);
    incomeInvitation.$("button:last-of-type").shouldHave(text("Decline")).click();
    return this;
  }

  @Step("Approve action")
  public FriendsPage approveAction() {
    popup.shouldBe(visible);
    submitActionBtn.click(ClickOptions.usingJavaScript());
    spinnerElement.should(disappear);
    return this;
  }

  @Step("Check that title 'There are no users yet' is present")
  public FriendsPage checkNoFriendsTitlePresent() {
    noFriendsTextElement.shouldBe(visible);
    return this;
  }

  @Step("Check that friends request table is absent")
  public FriendsPage checkFriendsRequestTableNotPresent() {
    friendsRequestTable.shouldNot(exist);
    return this;
  }

  @Step("Check that friends table is absent")
  public FriendsPage checkFriendsTableNotPresent() {
    friendsTable.shouldNot(exist);
    return this;
  }

  @Step("Check that friend {0} present in friends table")
  public FriendsPage checkFriendPresentInFriendsTable(String userName) {
    friendsTable.$(byText(userName)).shouldBe(visible);
    return this;
  }

  @Step("Check that friend {0} is NOT present in friends table")
  public FriendsPage checkFriendNotPresentInFriendsTable(String userName) {
    friendsTable.$(byText(userName)).shouldNot(exist);
    return this;
  }

  @Step("Check that income invitation from {0} is present in friends request table")
  public FriendsPage checkIncomeInvitationPresentInFriendsRequestsTable(String userName) {
    friendsRequestTable.$(byText(userName)).shouldBe(visible);
    return this;
  }

  @Step("Check that income invitation from {0} is NOT present in friends request table")
  public FriendsPage checkIncomeInvitationNotPresentInFriendsRequestsTable(String userName) {
    friendsRequestTable.$(byText(userName)).shouldNot(exist);
    return this;
  }

  private SelenideElement findTableRow(SelenideElement table, String username) {
    return table.$$("tr").findBy(text(username));
  }
}
