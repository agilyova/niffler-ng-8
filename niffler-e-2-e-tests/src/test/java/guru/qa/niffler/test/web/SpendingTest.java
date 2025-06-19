package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.AddSpendingPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Flaky;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.ZoneId;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User
  @ApiLogin
  @Test
  void userShouldBeAbleToAddNewSpending() {
    SpendJson spend = RandomDataUtils.randomSpending();

    Selenide.open(AddSpendingPage.URL, AddSpendingPage.class)
      .getSpendingForm()
      .setAmount(spend.amount())
      .selectCurrency(spend.currency())
      .createCategory(spend.category().name())
      .selectDate(spend.spendDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
      .setDescription(spend.description())
      .getParentPage()
      .submitSpendingCreation()
      .checkAlertMessage("New spending is successfully created")
      .getSpendingTable()
      .checkTableHaveExactSpends(spend);
  }

  @User(
    spendings = @Spend(
      category = "Учеба",
      description = "Обучение Niffler NG",
      amount = 89000.00,
      currency = CurrencyValues.RUB
    )
  )
  @ApiLogin
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
    final String newDescription = RandomDataUtils.randomString(12);

    Selenide.open(MainPage.URL, MainPage.class)
      .getSpendingTable()
      .editSpending(user.testData().spendings().getFirst().description())
      .getSpendingForm()
      .setDescription(newDescription)
      .getParentPage()
      .saveChanges()
      .checkAlertMessage("Spending is edited successfully")
      .getSpendingTable()
      .searchForSpending(newDescription)
      .checkThatTableContains(newDescription);
  }

  @User(
    spendings = {
      @Spend(
        category = "Обучение",
        description = "QA GURU Advanced",
        amount = 5000.00,
        currency = CurrencyValues.RUB
      ),
      @Spend(
        category = "Обучение",
        description = "Фигурное катание",
        amount = 2000.50,
        currency = CurrencyValues.RUB
      ),
      @Spend(
        category = "Подписки",
        description = "Yandex +",
        amount = 3250.00,
        currency = CurrencyValues.RUB
      )
    }
  )
  @ApiLogin
  @ScreenShotTest(value = "img/expected_diagram_grouping_by_category.png")
  void diagramComponentShouldGroupSpendingsByCategory(UserJson user, BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
      .getStatComponent()
      .checkDiagram(expected)
      .checkExactBubbles(
        new Bubble(Color.yellow, "Обучение 7000.5 ₽"),
        new Bubble(Color.green, "Подписки 3250 ₽")
      );
  }


  @User(
    categories = {
      @Category(name = "Обучение", archived = true),
      @Category(name = "Подписки", archived = true)
    },
    spendings = {
      @Spend(
        category = "Обучение",
        description = "QA GURU Advanced",
        amount = 5000.00,
        currency = CurrencyValues.RUB
      ),
      @Spend(
        category = "Подписки",
        description = "Yandex +",
        amount = 3250.00,
        currency = CurrencyValues.RUB
      ),
      @Spend(
        category = "Спорт",
        description = "Фигурное катание",
        amount = 2000.50,
        currency = CurrencyValues.RUB
      ),
    }
  )
  @ApiLogin
  @ScreenShotTest(value = "img/expected_diagram_grouping_by_archive_category.png")
  void diagramComponentShouldGroupSpendingsByArchiveCategory(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(MainPage.URL, MainPage.class)
      .getStatComponent()
      .checkDiagram(expected)
      .checkExactBubbles(
        new Bubble(Color.yellow, "Спорт 2000.5 ₽"),
        new Bubble(Color.green, "Archived 8250 ₽"));
  }

  @User(
    spendings = @Spend(
      category = "Учеба",
      description = "Обучение Niffler NG",
      amount = 10000.00,
      currency = CurrencyValues.RUB
    )
  )
  @ApiLogin
  @ScreenShotTest(value = "img/expected_diagram_after_edit.png")
  void diagramComponentShouldUpdatedAfterEditSpending(UserJson user, BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
      .getSpendingTable()
      .editSpending(user.testData().spendings().getFirst().description())
      .getSpendingForm()
      .setAmount(7550.00)
      .getParentPage()
      .saveChanges()
      .getStatComponent()
      .checkDiagram(expected)
      .checkExactBubbles(new Bubble(Color.yellow, "Учеба 7550 ₽"));
  }

  @User(
    spendings =
    @Spend(
      category = "Subscription",
      description = "Inteleji IDEA",
      amount = 1500.00,
      currency = CurrencyValues.RUB
    )
  )
  @ApiLogin
  @Flaky
  @ScreenShotTest("img/expected_diagram_empty.png")
  void diagramComponentShouldUpdatedAfterDeleteSpending(UserJson user, BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
      .getSpendingTable()
      .deleteSpending("Inteleji IDEA")
      .approveAction()
      .getStatComponent()
      .checkDiagram(expected);
  }

  @User(
    spendings = {
      @Spend(
        category = "Учеба",
        description = "Обучение Niffler NG",
        amount = 10000.00,
        currency = CurrencyValues.RUB
      ),
      @Spend(
        category = "Subscription",
        description = "IntelliJ IDEA",
        amount = 150.00,
        currency = CurrencyValues.EUR
      )
    }
  )
  @ApiLogin
  @Flaky
  @ScreenShotTest(value = "img/currency_filter_expected.png")
  void diagramComponentShouldUpdatedAfterFilterByCurrency(UserJson user, BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
      .getSpendingTable()
      .selectCurrency(CurrencyValues.EUR);

    new MainPage()
      .getStatComponent()
      .checkDiagram(expected)
      .checkExactBubbles(new Bubble(Color.yellow, "Subscription 150 €"));
  }

  @User(
    spendings = {
      @Spend(
        category = "Subscription",
        description = "Inteleji IDEA",
        amount = 150.00,
        currency = CurrencyValues.EUR
      ),
      @Spend(
        category = "Theater",
        description = "The Demon of Onegin",
        amount = 6000.50,
        currency = CurrencyValues.RUB
      ),
      @Spend(
        category = "Sport",
        description = "RG",
        amount = 5000.54,
        currency = CurrencyValues.RUB
      )
    }
  )
  @ApiLogin
  @Test()
  void tableShouldContainSpending(UserJson user) {
    Selenide.open(MainPage.URL, MainPage.class)
      .getSpendingTable()
      .checkTableHaveExactSpends(user.testData().spendings().toArray(SpendJson[]::new));
  }
}
