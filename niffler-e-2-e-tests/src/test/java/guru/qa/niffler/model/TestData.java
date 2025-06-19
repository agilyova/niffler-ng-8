package guru.qa.niffler.model;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record TestData(
  String password,
  List<CategoryJson> categories,
  List<SpendJson> spendings,
  List<UserJson> incomeRequests,
  List<UserJson> outcomeRequests,
  List<UserJson> friends
) {

  public List<String> friendsUsername() {
    return extractUsernames(friends);
  }

  public List<String> incomeRequestsUsername() {
    return extractUsernames(incomeRequests);
  }

  public List<String> outcomeRequestsUsername() {
    return extractUsernames(outcomeRequests);
  }

  public TestData(String password) {
    this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  @NotNull
  private List<String> extractUsernames(List<UserJson> users) {
    return users.stream().map(UserJson::username).toList();
  }
}
