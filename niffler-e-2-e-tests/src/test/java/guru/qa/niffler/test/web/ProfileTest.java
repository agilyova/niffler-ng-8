package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.validation.CategoryApiValidation;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

  @Test
  @User(
    userName = "test",
    categories = @Category()
  )
  void activeCategoryShouldBeAbleToArchive(CategoryJson category) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin("test", "test");

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .archiveCategory(category)
      .approveAction()
      .toggleShowArchiveCategories()
      .checkCategoryIsArchived(category)
      .checkAllCategoriesListContainsCategory(category);

    CategoryApiValidation.checkCategoryIsArchived(category);
  }

  @Test
  @User(
    userName = "test",
    categories = @Category(
      archived = true)
  )
  void archiveCategoryShouldBeAbleToUnArchive(CategoryJson category) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin("test", "test");

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .toggleShowArchiveCategories()
      .unArchiveCategory(category)
      .approveAction()
      .checkCategoryIsActive(category)
      .checkAllCategoriesListContainsCategory(category);

    CategoryApiValidation.checkCategoryIsActive(category);
  }

  @Test
  @User(
    userName = "test",
    categories = @Category()
  )
  void activeCategoryShouldBeAbleToEdit(CategoryJson category) {
    String newName = RandomDataUtils.randomCategoryName();

    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin("test", "test");

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .editCategory(category)
      .updateCategoryName(newName)
      .checkActiveCategoryListContainsCategory(newName);

    CategoryApiValidation.checkCurrentName(category, newName);
  }

  @Test
  @User(
    userName = "test",
    categories = @Category(
      archived = true
    )
  )
  void archiveCategoryShouldNotBeAbleToEdit(CategoryJson category) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin("test", "test");

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .toggleShowArchiveCategories()
      .checkEditCategoryButtonDoesntPresent(category);
  }
}
