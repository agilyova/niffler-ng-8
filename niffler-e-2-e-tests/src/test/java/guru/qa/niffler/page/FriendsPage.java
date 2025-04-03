package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {

  public static final String URL = Config.getInstance().frontUrl() + "people/friends";

  private final SelenideElement peopleTab = $("div[aria-label = 'People tabs']");
  private final SelenideElement spinnerElement = $(".MuiCircularProgress-root");
  private final SelenideElement noFriendsTextElement = $(byText("There are no users yet"));
  private final SelenideElement friendsRequestTable = $("table:has(#requests)");
  private final SelenideElement friendsTable = $("table:has(#friends)");

  public FriendsPage() {
    peopleTab.shouldBe(visible);
    spinnerElement.should(disappear);
  }

  public FriendsPage checkNoFriendsTitlePresent() {
    noFriendsTextElement.shouldBe(visible);
    return this;
  }

  public FriendsPage checkFriendsRequestTableNotPresent() {
    friendsRequestTable.shouldNot(exist);
    return this;
  }

  public FriendsPage checkFriendsTableNotPresent() {
    friendsTable.shouldNot(exist);
    return this;
  }

  public FriendsPage checkFriendPresentInFriendsTable(String userName) {
    friendsTable.$(byText(userName)).shouldBe(visible);
    return this;
  }

  public FriendsPage checkIncomeInvitationPresentInFriendsRequestsTable(String userName) {
    friendsRequestTable.$(byText(userName)).shouldBe(visible);
    return this;
  }
}
