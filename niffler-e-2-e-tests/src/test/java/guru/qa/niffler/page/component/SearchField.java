package guru.qa.niffler.page.component;

import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField>{

  public SearchField() {
    super($("input[aria-label='search']"));
  }

  public SearchField search(String description) {
    self.setValue(description).pressEnter();
    return this;
  }
}
