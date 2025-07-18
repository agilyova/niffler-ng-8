package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.model.enums.CurrencyValues;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Spend {
  String category();

  String description();

  double amount();

  CurrencyValues currency();
}
