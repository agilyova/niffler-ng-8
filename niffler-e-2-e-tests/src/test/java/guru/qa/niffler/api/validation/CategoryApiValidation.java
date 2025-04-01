package guru.qa.niffler.api.validation;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class CategoryApiValidation {
  private static boolean getCurrentArchiveStatusOfCategory(CategoryJson categoryJson) {
    List<CategoryJson> categoryList = new SpendApiClient().getCategories(categoryJson.username());
    return categoryList.stream()
      .filter(category -> category.name().equals(categoryJson.name()))
      .map(CategoryJson::archived)
      .findFirst()
      .orElse(false);
  }

  private static String getCurrentNameOfCategory(CategoryJson categoryJson) {
    List<CategoryJson> categoryList = new SpendApiClient().getCategories(categoryJson.username());
    return categoryList.stream()
      .filter(category -> category.id().equals(categoryJson.id()))
      .map(CategoryJson::name)
      .findFirst().get();
  }

  public static void checkCategoryIsActive(CategoryJson categoryJson) {
    boolean factArchiveStatus = getCurrentArchiveStatusOfCategory(categoryJson);
    Assertions.assertFalse(factArchiveStatus);
  }

  public static void checkCategoryIsArchived(CategoryJson categoryJson) {
    boolean factArchiveStatus = getCurrentArchiveStatusOfCategory(categoryJson);
    Assertions.assertTrue(factArchiveStatus);
  }

  public static void checkCurrentName(CategoryJson categoryJson, String newName) {
    String currentName = getCurrentNameOfCategory(categoryJson);
    Assertions.assertEquals(newName, currentName);
  }
}
