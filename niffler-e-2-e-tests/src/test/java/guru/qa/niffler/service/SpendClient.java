package guru.qa.niffler.service;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;

import java.util.List;

public interface SpendClient {

  SpendJson createSpend(SpendJson spend);

  CategoryJson createCategory(CategoryJson category);

  List<SpendJson> findSpendingsByCategory(CategoryJson categoryJson);

  void removeSpend(SpendJson spend);

  void removeCategory(CategoryJson category);
}
