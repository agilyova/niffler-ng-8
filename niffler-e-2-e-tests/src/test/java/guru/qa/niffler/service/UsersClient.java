package guru.qa.niffler.service;

import guru.qa.niffler.model.rest.UserJson;

public interface UsersClient {

  UserJson createUser(String username, String password);

  void createIncomeInvitations(UserJson targetUser, int count);

  void createOutcomeInvitations(UserJson targetUser, int count);

  void createFriends(UserJson targetUser, int count);

  UserJson findUserByUsername(String userName);

  void remove(UserJson user);
}
