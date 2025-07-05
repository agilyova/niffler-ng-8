package guru.qa.niffler.test.api;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

@Order(1)
public class GetAllUsersEmptyDbTest {
  UsersApiClient usersApiClient = new UsersApiClient();

  @Test
  void getAllUsersShouldReturnEmptyResulSet() {
    List<UserJson> allUsers = usersApiClient.getAllUsers();
    Assertions.assertTrue(allUsers.isEmpty(), "Empty DB should return emptyList");
  }
}