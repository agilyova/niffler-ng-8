package guru.qa.niffler.jupiter.annotation.meta;

import guru.qa.niffler.jupiter.extension.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@ExtendWith({
  BrowserExtension.class,
  AllureJunit5.class,
  UserExtension.class,
  CategoryExtension.class,
  SpendExtension.class,
  ApiLoginExtension.class,
  ScreenShotTestExtension.class
})
public @interface WebTest {
}


