package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.api.validation.CategoryApiValidation;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

  @Test
  @Category(userName = "test")
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
  @Category(
    userName = "test",
    archived = true)
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
  @Category(userName = "test")
  void activeCategoryShouldBeAbleToEdit(CategoryJson category) {
    String newName = new Faker().book().title();

    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin("test", "test");

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .editCategory(category)
      .updateCategoryName(newName)
      .checkActiveCategoryListContainsCategory(newName);

    CategoryApiValidation.checkCurrentName(category, newName);
  }

  @Test
  @Category(
    userName = "test",
    archived = true
  )
  void archiveCategoryShouldNotBeAbleToEdit(CategoryJson category) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin("test", "test");

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .toggleShowArchiveCategories()
      .checkEditCategoryButtonDoesntPresent(category);
  }
}
