package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.validation.CategoryApiValidation;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Flaky;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
public class ProfileTest {

  @Test
  @User(
    categories = @Category()
  )
  void activeCategoryShouldBeAbleToArchive(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .archiveCategory(user.testData().categories().getFirst())
      .approveAction()
      .toggleShowArchiveCategories()
      .checkCategoryIsArchived(user.testData().categories().getFirst())
      .checkAllCategoriesListContainsCategory(user.testData().categories().getFirst());

    CategoryApiValidation.checkCategoryIsArchived(user.testData().categories().getFirst());
  }

  @Flaky
  @Test
  @User(
    categories = @Category(
      archived = true)
  )
  void archiveCategoryShouldBeAbleToUnArchive(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .toggleShowArchiveCategories()
      .unArchiveCategory(user.testData().categories().getFirst())
      .approveAction()
      .checkCategoryIsActive(user.testData().categories().getFirst())
      .checkAllCategoriesListContainsCategory(user.testData().categories().getFirst());

    CategoryApiValidation.checkCategoryIsActive(user.testData().categories().getFirst());
  }

  @Test
  @User(
    categories = @Category()
  )
  void activeCategoryShouldBeAbleToEdit(UserJson user) {
    String newName = RandomDataUtils.randomCategoryName();

    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .editCategory(user.testData().categories().getFirst())
      .updateCategoryName(newName)
      .checkActiveCategoryListContainsCategory(newName);

    CategoryApiValidation.checkCurrentName(user.testData().categories().getFirst(), newName);
  }

  @Test
  @User(
    categories = @Category(
      archived = true
    )
  )
  void archiveCategoryShouldNotBeAbleToEdit(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .toggleShowArchiveCategories()
      .checkEditCategoryButtonDoesntPresent(user.testData().categories().getFirst());
  }

  @ScreenShotTest(
    "img/expected_avatar.png"
  )
  @User
  void profileImageShouldBeAbleToUploaded(UserJson user, BufferedImage expected) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(user.username(), user.testData().password())
      .goToProfilePage()
      .addProfileImage("img/avatar.png")
      .saveChanges()
      .refreshPage()
      .checkProfileImage(expected);
  }
}
