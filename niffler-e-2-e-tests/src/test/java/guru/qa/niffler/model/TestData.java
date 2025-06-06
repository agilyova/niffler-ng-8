package guru.qa.niffler.model;

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
  public TestData(String password) {
    this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }
}
