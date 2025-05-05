package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import okhttp3.OkHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//TODO временное решение
public class AuthUserApiClient {
  private static final Config CFG = Config.getInstance();

  private final OkHttpClient client = new OkHttpClient.Builder().build();
  private final Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(CFG.authUrl())
    .client(client)
    .addConverterFactory(JacksonConverterFactory.create())
    .build();

  private final AuthUserApi authUserApi = retrofit.create(AuthUserApi.class);

  private String getToken() {
    String token = "";
    Call<Void> call = authUserApi.getRegisterPage();
    try {
      Response<Void> response = call.execute();
      if (response.isSuccessful()) {
        List<String> cookies = response.headers().values("Set-Cookie");
        token = Arrays.stream(cookies.getFirst().split("; "))
          .filter(str -> str.startsWith("XSRF-TOKEN="))
          .map(str -> str.split("=")[1])
          .findFirst()
          .orElseThrow();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return token;
  }

  public void registerUser(String username, String password) {
    final Response response;
    String token = getToken();
    try {
      Call<Void> call = authUserApi.registerUser("XSRF-TOKEN=" + token, username, password, password, token);
      response = call.execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(HttpStatus.SC_CREATED, response.code());
  }
}


