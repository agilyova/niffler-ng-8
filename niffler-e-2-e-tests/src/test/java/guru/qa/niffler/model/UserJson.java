package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userData.FriendshipStatus;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.model.enums.CurrencyValues;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public record UserJson(
  @JsonProperty("id")
  UUID id,
  @JsonProperty("username")
  String username,
  @JsonProperty("firstname")
  String firstname,
  @JsonProperty("surname")
  String surname,
  @JsonProperty("fullname")
  String fullname,
  @JsonProperty("currency")
  CurrencyValues currency,
  @JsonProperty("photo")
  String photo,
  @JsonProperty("photoSmall")
  String photoSmall,
  @JsonIgnore
  FriendshipStatus friendshipStatus,
  @JsonIgnore
  TestData testData
) {

  public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity, @Nullable FriendshipStatus friendshipStatus) {
    return new UserJson(
      entity.getId(),
      entity.getUsername(),
      entity.getFirstname(),
      entity.getSurname(),
      entity.getFullname(),
      entity.getCurrency(),
      entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
      entity.getPhotoSmall() != null && entity.getPhotoSmall().length > 0 ? new String(entity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
      friendshipStatus,
      new TestData(
        null,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>()
      )
    );
  }

  public UserJson(@Nonnull String username) {
    this(null, username, null, null, null, null, null, null, null, null);
  }

  public UserJson withTestData(TestData testData) {
    return new UserJson(
      id,
      username,
      firstname,
      surname,
      fullname,
      currency,
      photo,
      photoSmall,
      friendshipStatus,
      testData
    );
  }

  public UserJson withPassword(String password) {
    if (testData == null) {
      return withTestData(
        new TestData(
          password,
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>()
        ));
    } else {
      return withTestData(
        new TestData(
          password,
          testData.categories(),
          testData.spendings(),
          testData.incomeRequests(),
          testData.outcomeRequests(),
          testData.friends()
        ));
    }
  }

  public @Nonnull UserJson addTestData(@Nonnull TestData testData) {
    return new UserJson(
      id,
      username,
      firstname,
      surname,
      fullname,
      currency,
      photo,
      photoSmall,
      friendshipStatus,
      testData
    );
  }
}
