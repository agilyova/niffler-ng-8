package guru.qa.niffler.api;

import guru.qa.niffler.model.rest.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserdataApi {

  @GET("internal/users/current")
  Call<UserJson> getUser(@Query("username") String username);

  @GET("internal/users/all")
  Call<List<UserJson>> getAllUsers(@Query("username") String username);

  @POST("internal/users/update")
  Call<UserJson> updateUserInfo(@Body UserJson user);

  @POST("internal/invitations/send")
  Call<UserJson> sendInvitation(
    @Query("username") String username,
    @Query("targetUsername") String targetUsername
  );

  @POST("internal/invitations/accept")
  Call<UserJson> acceptInvitation(
    @Query("username") String username,
    @Query("targetUsername") String targetUsername
  );

  @POST("internal/invitations/decline")
  Call<UserJson> declineInvitation(
    @Query("username") String username,
    @Query("targetUsername") String targetUsername
  );

  @GET("internal/friends/all")
  Call<List<UserJson>> friends(@Query("username") String username);

  @DELETE("internal/friends/remove")
  Call<Void> removeFriend(@Query("username") String username);
}
