package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {

  @GET("register")
  Call<Void> getRegisterPage();

  @FormUrlEncoded
  @POST("register")
  Call<Void> registerUser(@Field("username") String username,
                          @Field("password") String password,
                          @Field("passwordSubmit") String passwordSubmit,
                          @Field("_csrf") String csrf
  );

  @GET("oauth2/authorize")
  Call<Void> authorize(@Query("response_type") String response_type,
                       @Query("client_id") String client_id,
                       @Query("scope") String scope,
                       @Query("redirect_uri") String redirect_uri,
                       @Query("code_challenge") String code_challenge,
                       @Query("code_challenge_method") String code_challenge_method);

  @FormUrlEncoded
  @POST("login")
  Call<Void> login(@Field("_csrf") String csrf,
                     @Field("username") String username,
                     @Field("password") String password
  );

  @FormUrlEncoded
  @POST("oauth2/token")
  Call<JsonNode> getToken(@Field("code") String code,
                          @Field(value = "redirect_uri", encoded = true) String redirect_uri,
                          @Field(value = "code_verifier", encoded = true) String code_verifier,
                          @Field("grant_type") String grant_type,
                          @Field("client_id") String client_id);
}
