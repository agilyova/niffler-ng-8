package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import okhttp3.OkHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient {

  private static final Config CFG = Config.getInstance();

  private final OkHttpClient client = new OkHttpClient.Builder().build();
  private final Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(CFG.spendUrl())
    .client(client)
    .addConverterFactory(JacksonConverterFactory.create())
    .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);


  public SpendJson getSpend(String id) {
    final Response<SpendJson> response;
    try {
      response = spendApi.getSpend(id).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }

  public List<SpendJson> getAllSpends(String username,
                                      @Nullable CurrencyValues currencyValues,
                                      @Nullable String from,
                                      @Nullable String to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.getSpends(username).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body() != null
      ? response.body()
      : Collections.EMPTY_LIST;
  }

  public @Nullable SpendJson addSpend(SpendJson spend) {
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

  public @Nullable SpendJson updateSpend(SpendJson spendJson) {
    final Response<SpendJson> response;
    try {
      response = spendApi.updateSpend(spendJson).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }

  public void deleteSpend(String username, List<String> ids) {
    Response response;
    try {
      response = spendApi.deleteSpends(username, ids).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
  }

  public List<CategoryJson> getAllCategories(String userName) {
    Response<List<CategoryJson>> response;
    try {
      response = spendApi.getCategories(userName).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body() != null
      ? response.body()
      : Collections.EMPTY_LIST;
  }

  public @Nullable CategoryJson addCategory(CategoryJson categoryJson) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(categoryJson).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }

  public @Nullable CategoryJson updateCategory(CategoryJson categoryJson) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(categoryJson).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_OK, response.code());
    return response.body();
  }
}
