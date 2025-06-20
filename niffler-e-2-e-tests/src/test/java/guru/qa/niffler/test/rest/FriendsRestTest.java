package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.util.List;

import static guru.qa.niffler.model.enums.FriendshipStatus.*;
import static guru.qa.niffler.utils.RandomDataUtils.randomPassword;
import static guru.qa.niffler.utils.RandomDataUtils.randomUserName;

@RestTest
public class FriendsRestTest {

  @RegisterExtension
  static ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLoginExtension();

  private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

  @User(friends = 1, incomeInvitations = 2)
  @ApiLogin
  @Test
  void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String token) {
    List<UserJson> responseBody = gatewayApiClient.getAllFriends(token, null);
    Assertions.assertEquals(3, responseBody.size());
  }

  @User(friends = 1)
  @ApiLogin
  @Test
  void deleteFriend_whenUserHasOneFriend_friendIsRemoved(UserJson user, @Token String token) {
    String friendToDelete = user.testData().friends().removeFirst().username();
    Response<Void> response =
      gatewayApiClient.removeFriend(token, friendToDelete);

    Assertions.assertEquals(HttpStatus.SC_OK, response.code());

    List<UserJson> allFriends = gatewayApiClient.getAllFriends(token, null);
    Assertions.assertEquals(0, allFriends.size());
  }

  @User(friends = 2)
  @ApiLogin
  @Test
  void deleteFriend_whenUserHasSeveralFriends_removeExactFriend(UserJson user, @Token String token) {
    UserJson friendToDelete = user.testData().friends().removeFirst();
    Response<Void> response =
      gatewayApiClient.removeFriend(token, friendToDelete.username());

    Assertions.assertEquals(HttpStatus.SC_OK, response.code());

    List<UserJson> expectedFriends = user.testData().friends();
    List<UserJson> factFriends = gatewayApiClient.getAllFriends(token, null);

    Assertions.assertEquals(expectedFriends.size(), factFriends.size());
    Assertions.assertEquals(expectedFriends, factFriends);
  }

  @User
  @ApiLogin
  @Test
  void deleteFriend_whenNotUsersFriend_returnOk(@Token String token) {
    UserJson userToDelete = new UsersApiClient().createUser(randomUserName(), randomPassword());
    Response<Void> response = gatewayApiClient.removeFriend(token, userToDelete.username());

    Assertions.assertEquals(HttpStatus.SC_OK, response.code());
  }

  @User
  @ApiLogin
  @Test
  void deleteFriend_whenNotExistFriend_returnNotFound(@Token String token) {
    String userToDelete = RandomDataUtils.randomUserName();
    Response<Void> response = gatewayApiClient.removeFriend(token, userToDelete);

    Assertions.assertEquals(HttpStatus.SC_NOT_FOUND, response.code());
  }

  @User
  @ApiLogin
  @Test
  void deleteFriend_whenNoToken_returnUnAuthorized() {
    UserJson userToDelete = new UsersApiClient().createUser(randomUserName(), randomPassword());
    Response<Void> response = gatewayApiClient.removeFriend("", userToDelete.username());

    Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, response.code());
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void postAcceptInvitation_whenIncomeInvitationPresent_returnFriend(UserJson user, @Token String token) {
    UserJson requester = user.testData().incomeRequests().getFirst();

    Response<UserJson> response = gatewayApiClient.acceptInvitation(token, requester);
    UserJson expectedFriend = requester.withFriendShipStatus(FRIEND);

    Assertions.assertEquals(HttpStatus.SC_OK, response.code());
    Assertions.assertEquals(expectedFriend, response.body());

    List<UserJson> allFriends = gatewayApiClient.getAllFriends(token, null);
    Assertions.assertEquals(1, allFriends.size());
    Assertions.assertEquals(expectedFriend, allFriends.getFirst());
  }

  @User(incomeInvitations = 2)
  @ApiLogin
  @Test
  void postAcceptInvitation_whenSeveralIncomeInvitationsPresent_acceptExactInvitation(UserJson user, @Token String token) {
    UserJson requester = user.testData().incomeRequests().removeFirst();
    user.testData().friends().add(requester.withFriendShipStatus(FRIEND));

    Response<UserJson> response = gatewayApiClient.acceptInvitation(token, requester);
    Assertions.assertEquals(HttpStatus.SC_OK, response.code());
    Assertions.assertEquals(requester.withFriendShipStatus(FRIEND), response.body());

    List<UserJson> allFriends = gatewayApiClient.getAllFriends(token, null);
    List<UserJson> factFriends = allFriends.stream().filter(u -> u.friendshipStatus().equals(FRIEND)).toList();
    List<UserJson> factInvitations = allFriends.stream().filter(u -> u.friendshipStatus().equals(INVITE_RECEIVED)).toList();

    Assertions.assertEquals(user.testData().friends(), factFriends);
    Assertions.assertEquals(user.testData().incomeRequests(), factInvitations);
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void postDeclineInvitation_whenIncomeInvitationPresent_returnDeclineUser(UserJson user, @Token String token) {
    UserJson requester = user.testData().incomeRequests().getFirst();

    Response<UserJson> response = gatewayApiClient.declineInvitation(token, requester);

    Assertions.assertEquals(HttpStatus.SC_OK, response.code());
    Assertions.assertEquals(requester.withFriendShipStatus(null), response.body());

    List<UserJson> allFriends = gatewayApiClient.getAllFriends(token, null);
    Assertions.assertEquals(0, allFriends.size());
  }

  @User
  @ApiLogin
  @Test
  void postSendInvitation_whenInvitationSent_thenOutgoingAndIncomingRequestsAreCreated(UserJson user, @Token String token) {
    UsersApiClient usersApiClient = new UsersApiClient();
    UserJson addressee = new UsersApiClient().createUser(randomUserName(), randomPassword());
    Response<UserJson> response = gatewayApiClient.sendInvitation(token, addressee);

    Assertions.assertEquals(HttpStatus.SC_OK, response.code());
    Assertions.assertEquals(addressee.withFriendShipStatus(INVITE_SENT), response.body());

    UserJson factIncomeInvitation = usersApiClient.getIncomeInvitations(addressee.username()).getFirst();
    UserJson factOutcomeInvitation = usersApiClient.getOutComeInvitations(user.username()).getFirst();

    Assertions.assertEquals(addressee.withFriendShipStatus(INVITE_SENT), factOutcomeInvitation);
    Assertions.assertEquals(user.withFriendShipStatus(INVITE_RECEIVED).withTestData(null), factIncomeInvitation);
  }
}
