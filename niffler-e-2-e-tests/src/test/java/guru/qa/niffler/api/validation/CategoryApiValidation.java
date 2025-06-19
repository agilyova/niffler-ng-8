package guru.qa.niffler.api.validation;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.rest.CategoryJson;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class CategoryApiValidation {

  @Step("Check with API that category {0.name} is active")
  public static void checkCategoryIsActive(CategoryJson categoryJson) {
    boolean factArchiveStatus = getCurrentArchiveStatusOfCategory(categoryJson);
    Assertions.assertFalse(factArchiveStatus);
  }

  @Step("Check with API that category {0.name} is archived")
  public static void checkCategoryIsArchived(CategoryJson categoryJson) {
    boolean factArchiveStatus = getCurrentArchiveStatusOfCategory(categoryJson);
    Assertions.assertTrue(factArchiveStatus);
  }

  @Step("Check with API that category name updated from {0.name} to {1}")
  public static void checkCurrentName(CategoryJson categoryJson, String newName) {
    String currentName = getCurrentNameOfCategory(categoryJson);
    Assertions.assertEquals(newName, currentName);
  }

  private static boolean getCurrentArchiveStatusOfCategory(CategoryJson categoryJson) {
    List<CategoryJson> categoryList = new SpendApiClient().getAllCategories(categoryJson.username());
    return categoryList.stream()
      .filter(category -> category.name().equals(categoryJson.name()))
      .map(CategoryJson::archived)
      .findFirst()
      .orElse(false);
  }

  private static String getCurrentNameOfCategory(CategoryJson categoryJson) {
    List<CategoryJson> categoryList = new SpendApiClient().getAllCategories(categoryJson.username());
    return categoryList.stream()
      .filter(category -> category.id().equals(categoryJson.id()))
      .map(CategoryJson::name)
      .findFirst().get();
  }
}
