package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.Page;

import java.util.List;

@RestTest
public class FriendsV2RestTest {

  @RegisterExtension
  static ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLoginExtension();

  private final GatewayV2ApiClient gatewayApiClient = new GatewayV2ApiClient();

  @User(friends = 1, incomeInvitations = 2)
  @ApiLogin
  @Test
  void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(UserJson user, @Token String bearerToken) {
    Page<UserJson> responseBody = gatewayApiClient.getAllFriends("Bearer " + bearerToken, 0, 10, null);
    Assertions.assertEquals(3, responseBody.getContent().size());
  }
}
