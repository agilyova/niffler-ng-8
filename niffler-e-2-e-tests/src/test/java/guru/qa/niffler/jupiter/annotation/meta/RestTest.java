package guru.qa.niffler.jupiter.annotation.meta;

import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.SpendExtension;
import guru.qa.niffler.jupiter.extension.UserExtension;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@ExtendWith({
  AllureJunit5.class,
  UserExtension.class,
  CategoryExtension.class,
  SpendExtension.class,
})
public @interface RestTest {
}
