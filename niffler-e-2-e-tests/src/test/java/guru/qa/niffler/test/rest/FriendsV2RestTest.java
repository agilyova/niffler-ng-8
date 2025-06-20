package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.util.List;

import static guru.qa.niffler.model.enums.FriendshipStatus.FRIEND;
import static guru.qa.niffler.model.enums.FriendshipStatus.INVITE_RECEIVED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

@RestTest
public class FriendsV2RestTest {

  @RegisterExtension
  static ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLoginExtension();

  private final GatewayV2ApiClient gatewayApiClient = new GatewayV2ApiClient();

  @User(friends = 1)
  @ApiLogin
  @Test
  void getAllFriends_whenUserHasFriend_returnFriend(UserJson user, @Token String bearerToken) {
    UserJson expectedFriend = user.testData().friends().getFirst();

    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends(bearerToken, 0, 0, null);
    RestResponsePage<UserJson> responseBody = response.body();

    Assertions.assertEquals(SC_OK, response.code());
    Assertions.assertEquals(1, responseBody.getContent().size());
    Assertions.assertEquals(expectedFriend, responseBody.getContent().getFirst());
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void getAllFriends_whenUserHasIncomeInvitation_returnInvitation(UserJson user, @Token String bearerToken) {
    UserJson expectedInvitation = user.testData().incomeRequests().getFirst();

    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends(bearerToken, 0, 0, null);
    RestResponsePage<UserJson> responseBody = response.body();

    Assertions.assertEquals(SC_OK, response.code());
    Assertions.assertEquals(1, responseBody.getContent().size());
    Assertions.assertEquals(expectedInvitation, responseBody.getContent().getFirst());
  }

  @User(friends = 1, incomeInvitations = 1)
  @ApiLogin
  @Test
  void getAllFriends_whenUserHasFriendAndInvitation_returnFriendAndInvitation(UserJson user, @Token String bearerToken) {
    UserJson expectedFriend = user.testData().friends().getFirst();
    UserJson expectedInvitation = user.testData().incomeRequests().getFirst();

    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends(bearerToken, 0, 0, null);

    List<UserJson> list = response.body().getContent();
    UserJson factFriend = list.stream()
      .filter(u -> u.friendshipStatus().equals(FRIEND))
      .findFirst().orElseThrow();
    UserJson factIncomeInvitation = list.stream()
      .filter(u -> u.friendshipStatus().equals(INVITE_RECEIVED))
      .findFirst().orElseThrow();

    Assertions.assertEquals(SC_OK, response.code());
    Assertions.assertEquals(2, response.body().getContent().size());
    Assertions.assertEquals(expectedFriend, factFriend);
    Assertions.assertEquals(expectedInvitation, factIncomeInvitation);
  }

  @User
  @ApiLogin
  @Test
  void getAllFriends_whenUserHasNoFriends_returnEmptyContent(@Token String bearerToken) {
    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends(bearerToken, 0, 0, null);

    Assertions.assertEquals(SC_OK, response.code());
    Assertions.assertEquals(0, response.body().getContent().size());
  }

  @User(friends = 6)
  @ApiLogin
  @Test
  void getAllFriends_whenPageSizePresent_returnFixedAmountOfFriends(UserJson user, @Token String bearerToken) {
    List<UserJson> friends = user.testData().friends();
    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends(bearerToken, 0, 5, null);
    List<UserJson> factFriendsOnPage = response.body().getContent();

    Assertions.assertEquals(SC_OK, response.code());
    Assertions.assertEquals(5, response.body().getContent().size());
    Assertions.assertTrue(friends.containsAll(factFriendsOnPage));
  }

  @User(friends = 7)
  @ApiLogin
  @Test
  void getAllFriends_whenPageNumberPresent_returnFriendsOnPage(UserJson user, @Token String bearerToken) {
    List<UserJson> friends = user.testData().friends();
    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends(bearerToken, 1, 5, null);
    List<UserJson> factFriendsOnPage = response.body().getContent();

    Assertions.assertEquals(SC_OK, response.code());
    Assertions.assertEquals(2, response.body().getContent().size());
    Assertions.assertTrue(friends.containsAll(factFriendsOnPage));
  }

  @User(friends = 2, incomeInvitations = 2)
  @ApiLogin
  @Test
  void getAllFriends_whenSearchQueryPresent_returnFilteredList(UserJson user, @Token String bearerToken) {
    UserJson expectedFriend = user.testData().friends().getFirst();
    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends(bearerToken, 0, 0, expectedFriend.username());
    UserJson factFriend = response.body().getContent().getFirst();

    Assertions.assertEquals(SC_OK, response.code());
    Assertions.assertEquals(1, response.body().getContent().size());
    Assertions.assertEquals(expectedFriend, factFriend);
  }

  @User(friends = 1)
  @ApiLogin
  @Test
  void getAllFriends_whenRandomSearchQueryPresent_returnEmptyList(@Token String bearerToken) {
    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends(bearerToken, 0, 0, RandomDataUtils.randomUserName());

    Assertions.assertEquals(SC_OK, response.code());
    Assertions.assertEquals(0, response.body().getContent().size());
  }

  @User
  @ApiLogin
  @Test
  void getAllFriends_whenNoToken_returnUnAuthorized() {
    Response<RestResponsePage<UserJson>> response =
      gatewayApiClient.getAllFriends("", 0, 0, null);

    Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, response.code());
  }
}
