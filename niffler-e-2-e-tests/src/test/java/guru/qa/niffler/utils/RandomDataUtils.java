package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {
  private static final Faker faker = new Faker();

  public static String randomUserName() {
    return faker.name().username();
  }

  public static String randomString(int length) {
    return faker.lorem().characters(1, length);
  }

  public static String randomPassword(int minLength, int maxLength) {
    return faker.internet().password(minLength,maxLength);
  }

  public static String randomCategoryName() {
    return faker.commerce().productName();
  }
}
