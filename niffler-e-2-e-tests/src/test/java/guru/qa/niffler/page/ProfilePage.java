package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;

import static com.codeborne.selenide.Condition.*;
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


  public ProfilePage() {
    categoriesContainer.shouldBe(visible);
    spinnerElement.should(Condition.disappear);
  }

  public ProfilePage toggleShowArchiveCategories() {
    showArchivedCategoryToggle.click();
    return this;
  }

  public ProfilePage archiveCategory(CategoryJson categoryJson) {
    findCategory(categoryJson.name())
      .$(ARCHIVE_BUTTON_LOCATOR)
      .click();
    return this;
  }

  public ProfilePage unArchiveCategory(CategoryJson categoryJson) {
    findCategory(categoryJson.name())
      .$(UNARCHIVE_BUTTON_LOCATOR)
      .click();
    return this;
  }

  public ProfilePage editCategory(CategoryJson categoryJson) {
    findCategory(categoryJson.name())
      .$(EDIT_BUTTON_LOCATOR)
      .click();
    return this;
  }

  public ProfilePage updateCategoryName(String newName) {
    editCategoryNameInput.setValue(newName).pressEnter();
    spinnerElement.should(disappear);
    return this;
  }

  public ProfilePage approveAction() {
    submitActionBtn.click();
    spinnerElement.should(disappear);
    return this;
  }

  public ProfilePage checkAllCategoriesListContainsCategory(CategoryJson categoryJson) {
    findCategory(categoryJson.name()).should(exist);
    return this;
  }

  public ProfilePage checkCategoryIsActive(CategoryJson categoryJson) {
    SelenideElement categoryContainer = findCategory(categoryJson.name());

    categoryContainer.$(EDIT_BUTTON_LOCATOR).should(exist);
    categoryContainer.$(ARCHIVE_BUTTON_LOCATOR).should(exist);
    return this;
  }

  public ProfilePage checkCategoryIsArchived(CategoryJson categoryJson) {
    SelenideElement categoryContainer = findCategory(categoryJson.name());

    categoryContainer.$(EDIT_BUTTON_LOCATOR).shouldNot(exist);
    categoryContainer.$(UNARCHIVE_BUTTON_LOCATOR).should(exist);
    return this;
  }


  public ProfilePage checkActiveCategoryListContainsCategory(String categoryName) {
    findCategory(categoryName).should(exist);
    return this;
  }

  public ProfilePage checkEditCategoryButtonDoesntPresent(CategoryJson categoryJson) {
    findCategory(categoryJson.name())
      .$(EDIT_BUTTON_LOCATOR)
      .shouldNot(exist);
    return this;
  }

  private SelenideElement findCategory(String categoryName) {
    return categoryElements.find(text(categoryName));
  }
}
