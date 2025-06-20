package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient extends RestClient {

  private final GatewayApi gatewayApi;

  public GatewayApiClient() {
    super(CFG.gatewayUrl());
    this.gatewayApi = retrofit.create(GatewayApi.class);
  }

  @Step("Get all friends & income invitations using api/friends/all endpoint")
  public List<UserJson> getAllFriends(String token, @Nullable String searchQuery) {
    Response<List<UserJson>> response;
    try {
      response = gatewayApi.allFriends(token, searchQuery).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }

  @Step("Accept invitation using api/invitations/accept endpoint")
  public Response<UserJson> acceptInvitation(String token, UserJson friend) {
    Response<UserJson> response;
    try {
      response = gatewayApi.acceptInvitation(token, friend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    return response;
  }

  @Step("Decline invitation using api/invitations/decline endpoint")
  public Response<UserJson> declineInvitation(String token, UserJson friend) {
    Response<UserJson> response;
    try {
      response = gatewayApi.declineInvitation(token, friend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    return response;
  }

  @Step("Send invitation using api/invitations/send endpoint")
  public Response<UserJson> sendInvitation(String token, UserJson friend) {
    Response<UserJson> response;
    try {
      response = gatewayApi.sendInvitation(token, friend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    return response;
  }

  @Step("Remove friend using api/friends/remove endpoint")
  public Response<Void> removeFriend(String token, @Nullable String targetUsername) {
    Response<Void> response;
    try {
      response = gatewayApi.removeFriend(token, targetUsername).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    return response;
  }
}
