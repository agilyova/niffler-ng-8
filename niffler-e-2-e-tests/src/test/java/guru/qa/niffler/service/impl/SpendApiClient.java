package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient extends RestClient implements SpendClient {

  private final SpendApi spendApi;

  public SpendApiClient() {
    super(CFG.spendUrl());
    this.spendApi = retrofit.create(SpendApi.class);
  }

  @Override
  @Step("Create spend with API")
  public SpendJson createSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend)
        .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_CREATED, response.code());
    return response.body();
  }

  @Override
  @Step("Create category with API")
  public CategoryJson createCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }

  @Override
  @Step("Find spend by category with API")
  public List<SpendJson> findSpendingsByCategory(CategoryJson category) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.getSpends(category.username()).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }

  @Override
  @Step("Remove spend with API")
  public void removeSpend(SpendJson spend) {
    Response response;
    try {
      response = spendApi.deleteSpends(spend.username(), List.of(String.valueOf(spend.id()))).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_ACCEPTED, response.code());
  }

  @Override
  @Step("Remove category with API")
  public void removeCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Method not implemented yet");
  }

  @Step("Get all categories with API")
  public List<CategoryJson> getAllCategories(String username) {
    Response<List<CategoryJson>> response;
    try {
      response = spendApi.getCategories(username).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }

  @Step("Get all spends with API")
  public List<SpendJson> getAllSpends(String username) {
    Response<List<SpendJson>> response;
    try {
      response = spendApi.getSpends(username).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }
}
