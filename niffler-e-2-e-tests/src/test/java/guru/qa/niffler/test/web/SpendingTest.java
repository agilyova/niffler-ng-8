package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;

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

  @User(
    spendings = @Spend(
      category = "Учеба",
      description = "Обучение Niffler NG",
      amount = 89000.00,
      currency = CurrencyValues.RUB
    )
  )
  @ScreenShotTest("img/expected_stat.png")
  void statComponentShouldBePresent(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
      .doLogin(user.username(), user.testData().password());

    BufferedImage actual = ImageIO.read($("canvas[role = 'img']").screenshot());
    Assertions.assertFalse(
      new ScreenDiffResult(
        expected,
        actual
      )
    );
  }
}
