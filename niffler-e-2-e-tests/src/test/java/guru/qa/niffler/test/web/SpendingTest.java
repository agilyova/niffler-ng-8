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
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
    userName = "test",
    spendings = @Spend(
      category = "Учеба",
      description = "Обучение Niffler NG",
      amount = 89000.00,
      currency = CurrencyValues.RUB
    )
  )
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
    final String newDescription = RandomDataUtils.randomString(12);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
      .doLogin(user.username(), "test")
      .editSpending(user.testData().spendings().getFirst().description())
      .editDescription(newDescription);

    new MainPage().
      searchForSpending(newDescription).
      checkThatTableContains(newDescription);
  }
}
