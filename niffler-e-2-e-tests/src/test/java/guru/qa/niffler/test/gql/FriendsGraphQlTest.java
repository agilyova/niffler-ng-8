package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import quru.qa.FriendsCategoriesQuery;
import quru.qa.FriendsQuery;
import quru.qa.NestedFriendsDepth2Query;

public class FriendsGraphQlTest extends BaseGraphQlTest {
  @User(friends = 1)
  @ApiLogin
  @Test
  void userQuery_withFriends_shouldNotReturnFriendsCategories(@Token String bearerToken) {
    ApolloCall<FriendsCategoriesQuery.Data> categoriesCall = apolloClient.query(
      new FriendsCategoriesQuery(0, 10, null, null)
    ).addHttpHeader("authorization", bearerToken);

    ApolloResponse<FriendsCategoriesQuery.Data> response = Rx2Apollo.single(categoriesCall).blockingGet();
    final FriendsCategoriesQuery.Data data = response.dataOrThrow();
    FriendsCategoriesQuery.User user = data.user;

    Assertions.assertEquals("Can`t query categories for another user",response.errors.getFirst().getMessage());
    Assertions.assertNull(user.friends.edges.getFirst());
  }

  @User(friends = 1)
  @ApiLogin
  @Test
  void userQuery_withNested2Friends_shouldNotReturnNestedFriends(@Token String bearerToken) {
    ApolloCall<NestedFriendsDepth2Query.Data> friendsCall = apolloClient.query(
      new NestedFriendsDepth2Query(0, 10, null, null)
    ).addHttpHeader("authorization", bearerToken);

    ApolloResponse<NestedFriendsDepth2Query.Data> response = Rx2Apollo.single(friendsCall).blockingGet();

    Assertions.assertEquals("Recursive friends queries are not supported",response.errors.getFirst().getMessage());
    Assertions.assertNull(response.data);
  }

  @User(friends = 1)
  @ApiLogin
  @Test
  void userQuery_withFriends_shouldReturnFriends(@Token String bearerToken, UserJson user) {
    ApolloCall<FriendsQuery.Data> friendsCall = apolloClient.query(
      new FriendsQuery(0, 10, null, null)
    ).addHttpHeader("authorization", bearerToken);

    ApolloResponse<FriendsQuery.Data> response = Rx2Apollo.single(friendsCall).blockingGet();
    final FriendsQuery.Data data = response.dataOrThrow();
    FriendsQuery.User responseUser = data.user;

    String expectedFriendsUsername = user.testData().friends().getFirst().username();
    String factFriendsUsername = responseUser.friends.edges.getFirst().node.username;

    Assertions.assertEquals(expectedFriendsUsername, factFriendsUsername);
  }
}
