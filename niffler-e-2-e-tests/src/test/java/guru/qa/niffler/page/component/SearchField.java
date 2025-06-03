package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.$;

public class SearchField {
  private final SelenideElement searchInputElement = $("input[aria-label='search']");

  public SearchField search(String description) {
    searchInputElement.setValue(description).pressEnter();
    return this;
  }
}
