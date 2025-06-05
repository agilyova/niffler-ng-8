package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.RestClient;
import okhttp3.OkHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GhApiClient extends RestClient {

  public static final String GITHUB_TOKEN = "GITHUB_TOKEN";

  private final GhApi ghdApi;

  public GhApiClient() {
    super(CFG.ghUrl());
    this.ghdApi = retrofit.create(GhApi.class);
  }

  public String issueState(String issueNumber) {
    Response<JsonNode> response;
    try {
      response = ghdApi.issue(
        "Bearer" + System.getenv(GITHUB_TOKEN),
        issueNumber
      ).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }

    assertEquals(HttpStatus.SC_OK, response.code());
    return Objects.requireNonNull(response.body()).get("state").asText();
  }
}
