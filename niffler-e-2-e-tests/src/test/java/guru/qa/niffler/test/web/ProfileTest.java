package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.validation.CategoryApiValidation;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Flaky;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
public class ProfileTest {


  @User(
    categories = @Category()
  )
  @ApiLogin
  @Test
  void activeCategoryShouldBeAbleToArchive(UserJson user) {
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
  @ApiLogin
  void archiveCategoryShouldBeAbleToUnArchive(UserJson user) {
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
  @ApiLogin
  void activeCategoryShouldBeAbleToEdit(UserJson user) {
    String newName = RandomDataUtils.randomCategoryName();

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
  @ApiLogin
  void archiveCategoryShouldNotBeAbleToEdit(UserJson user) {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .toggleShowArchiveCategories()
      .checkEditCategoryButtonDoesntPresent(user.testData().categories().getFirst());
  }

  @ScreenShotTest(
    value = "img/expected_avatar.png"
  )
  @User
  @ApiLogin
  void profileImageShouldBeAbleToUploaded(BufferedImage expected) {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .addProfileImage("img/avatar.png")
      .saveChanges()
      .refreshPage()
      .checkProfileImage(expected);
  }

  @Test
  @User
  @ApiLogin
  void profileDataShouldBeAbleToEdit() {
    String name = RandomDataUtils.randomName();

    Selenide.open(ProfilePage.URL, ProfilePage.class)
      .updateProfileName(name)
      .saveChanges()
      .checkAlertMessage("Profile successfully updated")
      .refreshPage()
      .checkProfileName(name);
  }
}
