package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
    spendings = @Spend(
      category = "Учеба",
      description = "Обучение Niffler NG",
      amount = 89000.00,
      currency = CurrencyValues.RUB
    )
  )
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
    //System.out.println(user);
    final String newDescription = "Обучение Niffler NG";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
      .doLogin(user.username(), user.testData().password())
      .editSpending(user.testData().spendings().getFirst().description())
      .editDescription(newDescription);

    new MainPage().checkThatTableContains(newDescription);
  }
}
