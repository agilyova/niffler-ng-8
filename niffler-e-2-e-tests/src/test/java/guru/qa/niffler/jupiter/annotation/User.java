package guru.qa.niffler.jupiter.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface User {
  String userName() default "";

  int incomeInvitations() default 0;

  int outcomeInvitations() default 0;

  int friends() default 0;

  Category[] categories() default {};

  Spend[] spendings() default {};
}
