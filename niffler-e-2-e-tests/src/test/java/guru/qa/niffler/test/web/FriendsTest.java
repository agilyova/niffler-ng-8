package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {

  @User
  @Test
  void friendsTableShouldBeEmptyFotNewUser(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password())
      .getHeader()
      .goToFriendsPage()
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
      .doLogin(user.username(), user.testData().password())
      .getHeader()
      .goToFriendsPage()
      .checkFriendPresentInFriendsTable(user.testData().friends().getFirst().username());
  }

  @User(
    amountOfOutcomeInvitations = 1
  )
  @Test
  void outcomeInvitationShouldBePresentInAllPeopleTable(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password())
      .getHeader()
      .goToAllPeoplePage()
      .searchForPerson(user.testData().outcomeRequests().getFirst().username())
      .checkOutcomeInvitationPresentInAllPeopleTable(user.testData().outcomeRequests().getFirst().username());
  }

  @User(
    amountOfIncomeInvitations = 1
  )
  @Test
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password())
      .getHeader()
      .goToFriendsPage()
      .checkIncomeInvitationPresentInFriendsRequestsTable(user.testData().incomeRequests().getFirst().username());
  }

  @User(
    amountOfIncomeInvitations = 1
  )
  @Test
  void incomeInvitationShouldBeAbleToAccept(UserJson user) {
    String friendUserName = user.testData().incomeRequests().getFirst().username();

    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password())
      .getHeader()
      .goToFriendsPage()
      .acceptIncomeInvitation(friendUserName)
      .checkFriendPresentInFriendsTable(friendUserName);
  }

  @User(
    amountOfIncomeInvitations = 1
  )
  @Test
  void incomeInvitationShouldBeAbleToDecline(UserJson user) {
    String friendUserName = user.testData().incomeRequests().getFirst().username();

    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password())
      .getHeader()
      .goToFriendsPage()
      .declineIncomeInvitation(friendUserName)
      .approveAction()
      .checkFriendNotPresentInFriendsTable(friendUserName)
      .checkIncomeInvitationNotPresentInFriendsRequestsTable(friendUserName);
  }
}
