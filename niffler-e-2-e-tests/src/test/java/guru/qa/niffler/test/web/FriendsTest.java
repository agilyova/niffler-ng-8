package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {

  @User
  @ApiLogin
  @Test
  void friendsTableShouldBeEmptyFotNewUser() {
    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkNoFriendsTitlePresent()
      .checkFriendsRequestTableNotPresent()
      .checkFriendsTableNotPresent();
  }

  @User(
    amountOfFriends = 1
  )
  @ApiLogin
  @Test
  void friendsShouldBePresentInFriendsTable(UserJson user) {
    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkFriendPresentInFriendsTable(user.testData().friends().getFirst().username());
  }

  @User(
    amountOfOutcomeInvitations = 1
  )
  @ApiLogin
  @Test
  void outcomeInvitationShouldBePresentInAllPeopleTable(UserJson user) {
    Selenide.open(AllPeoplePage.URL, AllPeoplePage.class)
      .searchForPerson(user.testData().outcomeRequests().getFirst().username())
      .checkOutcomeInvitationPresentInAllPeopleTable(user.testData().outcomeRequests().getFirst().username());
  }

  @User(
    amountOfIncomeInvitations = 1
  )
  @ApiLogin
  @Test
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkIncomeInvitationPresentInFriendsRequestsTable(user.testData().incomeRequests().getFirst().username());
  }

  @User(
    amountOfIncomeInvitations = 1
  )
  @ApiLogin
  @Test
  void incomeInvitationShouldBeAbleToAccept(UserJson user) {
    String friendUserName = user.testData().incomeRequests().getFirst().username();

    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .acceptIncomeInvitation(friendUserName)
      .checkFriendPresentInFriendsTable(friendUserName);
  }

  @User(
    amountOfIncomeInvitations = 1
  )
  @ApiLogin
  @Test
  void incomeInvitationShouldBeAbleToDecline(UserJson user) {
    String friendUserName = user.testData().incomeRequests().getFirst().username();

    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .declineIncomeInvitation(friendUserName)
      .approveAction()
      .checkFriendNotPresentInFriendsTable(friendUserName)
      .checkIncomeInvitationNotPresentInFriendsRequestsTable(friendUserName);
  }
}
