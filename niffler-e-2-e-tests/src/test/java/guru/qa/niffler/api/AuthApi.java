package guru.qa.niffler.api;

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
}
