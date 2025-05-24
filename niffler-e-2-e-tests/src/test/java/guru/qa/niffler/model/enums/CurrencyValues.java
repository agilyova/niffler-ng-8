package guru.qa.niffler.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CurrencyValues {
  RUB("₽"),
  USD("$"),
  EUR("€"),
  KZT("₸");

  public final String alias;
}
