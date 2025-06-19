package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.validation.GatewayV2Api;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.data.domain.Page;
import retrofit2.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayV2ApiClient extends RestClient {

  private final GatewayV2Api gatewayV2Api;

  public GatewayV2ApiClient() {
    super(CFG.gatewayUrl());
    this.gatewayV2Api = retrofit.create(GatewayV2Api.class);
  }

  @Step("Get all friends & income invitations using api/friends/all with API")
  public RestResponsePage<UserJson> getAllFriends(String token, int page, int size, @Nullable String searchQuery) {
    Response<RestResponsePage<UserJson>> response;
    try {
      response = gatewayV2Api.allFriends(token, page, size, searchQuery).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }
}
