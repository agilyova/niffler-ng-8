package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.SpendExtension;
import guru.qa.niffler.jupiter.extension.UserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
@ExtendWith({UserExtension.class, CategoryExtension.class, SpendExtension.class})
public @interface User {
  String userName() default "";

  int amountOfIncomeInvitations() default 0;

  int amountOfOutcomeInvitations() default 0;

  int amountOfFriends() default 0;

  Category[] categories() default {};

  Spend[] spendings() default {};
}
