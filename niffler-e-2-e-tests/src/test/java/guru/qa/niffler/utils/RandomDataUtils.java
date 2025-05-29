package guru.qa.niffler.utils;

import com.github.javafaker.Faker;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.enums.CurrencyValues;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomDataUtils {
  private static final Faker faker = new Faker();

  public static String randomUserName() {
    return faker.name().username();
  }

  public static String randomName() {
    return faker.name().name();
  }

  public static String randomString(int length) {
    return faker.lorem().characters(1, length);
  }

  public static double randomDouble() {
    return faker.number().randomDouble(2, 1, 100000);
  }

  public static String randomPassword(int minLength, int maxLength) {
    return faker.internet().password(minLength, maxLength);
  }

  public static String randomCategoryName() {
    return faker.commerce().productName();
  }

  public static CurrencyValues randomCurrency() {
    CurrencyValues[] currencyValues = CurrencyValues.values();
    return currencyValues[new Random().nextInt(currencyValues.length)];
  }

  public static SpendJson randomSpending() {
    return new SpendJson(
      null,
      faker.date().past(3600, TimeUnit.DAYS),
      new CategoryJson(
        null,
        randomCategoryName(),
        null,
        false
      ),
      randomCurrency(),
      randomDouble(),
      randomString(20),
      null);
  }
}
