package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;

@WebTest
public class FriendsTest {

  @Test
  void friendsTableShouldBeEmptyFotNewUser(@UserType StaticUser user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.userName(), user.password());

    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkNoFriendsTitlePresent()
      .checkFriendsRequestTableNotPresent()
      .checkFriendsTableNotPresent();
  }

  @Test
  void friendsShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.userName(), user.password());

    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkFriendPresentInFriendsTable(user.friend());
  }

  @Test
  void outcomeInvitationBePresentInAllPeopleTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.userName(), user.password());

    Selenide.open(AllPeoplePage.URL, AllPeoplePage.class)
      .checkOutComeInvitationPresentInAllPeopleTable(user.outcome());
  }

  @Test
  void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.userName(), user.password());

    Selenide.open(FriendsPage.URL, FriendsPage.class)
      .checkIncomeInvitationPresentInFriendsRequestsTable(user.income());
  }
}
