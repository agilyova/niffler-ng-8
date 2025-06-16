package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {

  private final SelenideElement spendingTableEl = $("#spendings");

  @Getter
  private final Header header = new Header();

  @Getter
  private final SpendingTable spendingTable = new SpendingTable();

  @Getter
  private final StatComponent statComponent = new StatComponent();

  public MainPage() {
    spendingTableEl.shouldBe(visible);
  }
}
