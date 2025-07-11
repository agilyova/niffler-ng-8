package guru.qa.niffler.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CurrencyValues {
  RUB("₽", 1.0),
  USD("$", 66.67),
  EUR("€", 72.0),
  KZT("₸", .14);

  public final String alias;
  public final Double tuRub;
}
