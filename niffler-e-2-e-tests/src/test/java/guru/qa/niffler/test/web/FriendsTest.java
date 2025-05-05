package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {

  @User
  @Test
  void friendsTableShouldBeEmptyFotNewUser(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkNoFriendsTitlePresent()
      .checkFriendsRequestTableNotPresent()
      .checkFriendsTableNotPresent();
  }

  @User(
    amountOfFriends = 1
  )
  @Test
  void friendsShouldBePresentInFriendsTable(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkFriendPresentInFriendsTable(user.testData().friends().getFirst().username());
  }

  @User(
    amountOfOutcomeInvitations = 1
  )
  @Test
  void outcomeInvitationShouldBePresentInAllPeopleTable(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    Selenide.open(AllPeoplePage.URL, AllPeoplePage.class)
      .checkOutComeInvitationPresentInAllPeopleTable(user.testData().outcomeRequests().getFirst().username());
  }

  @User(
    amountOfIncomeInvitations = 1
  )
  @Test
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkIncomeInvitationPresentInFriendsRequestsTable(user.testData().incomeRequests().getFirst().username());
  }
}
