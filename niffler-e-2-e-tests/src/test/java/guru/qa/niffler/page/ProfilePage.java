package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;


public class ProfilePage {
  public static final String URL = Config.getInstance().frontUrl() + "profile";

  public static final String EDIT_BUTTON_LOCATOR = "button[aria-label='Edit category']";
  public static final String ARCHIVE_BUTTON_LOCATOR = "button[aria-label='Archive category']";
  public static final String UNARCHIVE_BUTTON_LOCATOR = "button[aria-label='Unarchive category']";

  private final SelenideElement showArchivedCategoryToggle = $("input[type='checkbox']");
  private final SelenideElement categoriesContainer = $(".MuiGrid-container:has(#category)");
  private final ElementsCollection categoryElements = categoriesContainer.$$(".MuiGrid-item>.MuiBox-root");
  private final SelenideElement spinnerElement = categoriesContainer.$(".MuiCircularProgress-root");
  private final SelenideElement editCategoryNameInput = $("input[placeholder = 'Edit category']");
  private final SelenideElement submitActionBtn = $(".MuiDialogActions-root>button:last-of-type");
  private final SelenideElement confirmDialog = $("div[role = 'dialog']");
  private final SelenideElement uploadImageInput = $("#image__input");
  private final SelenideElement saveChangesButton = $(byText("Save changes"));
  private final SelenideElement profileImageElement = $("form .MuiAvatar-img");
  private final SelenideElement headerImageElement = $("header .MuiAvatar-img");
  private final SelenideElement nameInput = $("#name");


  public ProfilePage() {
    categoriesContainer.shouldBe(visible);
    spinnerElement.should(Condition.disappear);
  }

  @Step("Switch \"Show archived\" button")
  public ProfilePage toggleShowArchiveCategories() {
    showArchivedCategoryToggle.click();
    return this;
  }

  @Step("Archive category with name: {0.name}")
  public ProfilePage archiveCategory(CategoryJson categoryJson) {

    findCategory(categoryJson.name())
      .$(ARCHIVE_BUTTON_LOCATOR)
      .click();
    confirmDialog.should(appear);
    return this;
  }

  @Step("Unarchive category with name: {0.name}")
  public ProfilePage unArchiveCategory(CategoryJson categoryJson) {
    findCategory(categoryJson.name())
      .$(UNARCHIVE_BUTTON_LOCATOR)
      .click();
    confirmDialog.should(appear);
    return this;
  }

  @Step("Edit category with name: {0.name}")
  public ProfilePage editCategory(CategoryJson categoryJson) {
    findCategory(categoryJson.name())
      .$(EDIT_BUTTON_LOCATOR)
      .click();
    return this;
  }

  @Step("Update category name on {0}")
  public ProfilePage updateCategoryName(String newName) {
    editCategoryNameInput.setValue(newName).pressEnter();
    spinnerElement.should(disappear);
    return this;
  }

  @Step("Approve action")
  public ProfilePage approveAction() {
    submitActionBtn.click();
    spinnerElement.should(disappear);
    return this;
  }

  @Step("Add profile image. Image path: {0}")
  public ProfilePage addProfileImage(String path) {
    uploadImageInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Update profile name on {0}")
  public ProfilePage updateProfileName(String name) {
    nameInput.setValue(name);
    return this;
  }

  @Step("Save changes")
  public ProfilePage saveChanges() {
    saveChangesButton.click();
    saveChangesButton.shouldBe(enabled);
    return this;
  }

  @Step("Refresh page")
  public ProfilePage refreshPage() {
    Selenide.refresh();
    return this;
  }

  @Step("Check that All categories list contains category with name {0.name}")
  public ProfilePage checkAllCategoriesListContainsCategory(CategoryJson categoryJson) {
    findCategory(categoryJson.name()).should(exist);
    return this;
  }

  @Step("Check that category with name {0.name} is active")
  public ProfilePage checkCategoryIsActive(CategoryJson categoryJson) {
    SelenideElement categoryContainer = findCategory(categoryJson.name());

    categoryContainer.$(EDIT_BUTTON_LOCATOR).should(exist);
    categoryContainer.$(ARCHIVE_BUTTON_LOCATOR).should(exist);
    return this;
  }

  @Step("Check that category with name {0.name} is archived")
  public ProfilePage checkCategoryIsArchived(CategoryJson categoryJson) {
    SelenideElement categoryContainer = findCategory(categoryJson.name());

    categoryContainer.$(EDIT_BUTTON_LOCATOR).shouldNot(exist);
    categoryContainer.$(UNARCHIVE_BUTTON_LOCATOR).should(exist);
    return this;
  }

  @Step("Check that active categories list contains category with name {0}")
  public ProfilePage checkActiveCategoryListContainsCategory(String categoryName) {
    findCategory(categoryName).should(exist);
    return this;
  }

  @Step("Check that edit button is not present for category {0.name}")
  public ProfilePage checkEditCategoryButtonDoesntPresent(CategoryJson categoryJson) {
    findCategory(categoryJson.name())
      .$(EDIT_BUTTON_LOCATOR)
      .shouldNot(exist);
    return this;
  }

  @Step("Check profile image is present and correct")
  @SneakyThrows
  public ProfilePage checkProfileImage(BufferedImage expected) {
    Selenide.sleep(3000);
    BufferedImage actual = ImageIO.read(profileImageElement.screenshot());

    Assertions.assertFalse(
      new ScreenDiffResult(
        expected,
        actual
      )
    );
    return this;
  }

  @Step("Check profile name is {0}")
  public ProfilePage checkProfileName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }

  private SelenideElement findCategory(String categoryName) {
    return categoryElements.find(text(categoryName));
  }
}
