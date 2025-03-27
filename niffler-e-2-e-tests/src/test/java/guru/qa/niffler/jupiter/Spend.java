package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({CreateSpendExtension.class, ResolveSpendingExtension.class})
public @interface Spend {
  String username();

  String category();

  String description();

  double amount();

  CurrencyValues currency();
}
