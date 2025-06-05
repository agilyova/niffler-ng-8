package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Header extends BaseComponent<Header> {

  private final SelenideElement logo = self.$("a[href = '/main']");
  private final SelenideElement spendingButton = self.$("a[href = '/spending']");
  private final SelenideElement menuButton = self.$("button[aria-label= 'Menu']");
  private final ElementsCollection menuItems = $$("li[role = 'menuitem']");

  public Header() {
    super($("#root header"));
  }

  @Step("Click on logo in Header")
  public MainPage goToMainPage() {
    logo.click();
    return new MainPage();
  }

  @Step("Click New Spending button in header ")
  public AddSpendingPage goToAddSpendingPage() {
    spendingButton.click();
    return new AddSpendingPage();
  }

  @Step("Select Profile item in header menu")
  public ProfilePage goToProfilePage() {
    menuButton.click();
    menuItems.findBy(text("Profile")).click();
    return new ProfilePage();
  }

  @Step("Select Friends item in header menu")
  public FriendsPage goToFriendsPage() {
    menuButton.click();
    menuItems.findBy(text("Friends")).click();
    return new FriendsPage();
  }

  @Step("Select All People item in header menu")
  public AllPeoplePage goToAllPeoplePage() {
    menuButton.click();
    menuItems.findBy(text("All people")).click();
    return new AllPeoplePage();
  }

  @Step("Select Sign out item in header menu")
  public LoginPage signOut() {
    menuButton.click();
    menuItems.findBy(text("Sign out")).click();
    return new LoginPage();
  }
}
