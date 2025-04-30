package guru.qa.niffler.service;

import guru.qa.niffler.model.SpendJson;

public interface SpendClient {

  SpendJson create(SpendJson spend);

  void remove(SpendJson spend);
}
