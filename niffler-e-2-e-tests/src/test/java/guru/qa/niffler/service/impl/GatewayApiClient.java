package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.SpendClient;
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

  @Step("Get all friends & income invitations using api/friends/all with API")
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
}
