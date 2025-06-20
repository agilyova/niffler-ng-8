package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayV2Api;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nullable;
import java.io.IOException;

public class GatewayV2ApiClient extends RestClient {

  private final GatewayV2Api gatewayV2Api;

  public GatewayV2ApiClient() {
    super(CFG.gatewayUrl());
    this.gatewayV2Api = retrofit.create(GatewayV2Api.class);
  }

  @Step("Get all friends & income invitations using api/friends/all with API")
  public Response<RestResponsePage<UserJson>> getAllFriends(String token, int page, int size, @Nullable String searchQuery) {
    Response<RestResponsePage<UserJson>> response;
    try {
      response = gatewayV2Api.allFriends(token, page, size, searchQuery).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    return response;
  }
}
