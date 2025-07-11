package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import quru.qa.StatQuery;
import quru.qa.type.FilterPeriod;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static guru.qa.niffler.model.enums.CurrencyValues.EUR;
import static guru.qa.niffler.model.enums.CurrencyValues.RUB;

public class StatGraphQlTest extends BaseGraphQlTest {

  @User
  @ApiLogin
  @Test
  void queryStat_whenNoData_ReturnZero(@Token String bearerToken) {
    StatQuery.Stat stat = getStatQueryResponseData(bearerToken, null, null, null);

    Assertions.assertEquals(0.0, stat.total);
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
        currency = RUB
      ),
      @Spend(
        category = "Подписки",
        description = "Yandex +",
        amount = 3250.00,
        currency = RUB
      ),
      @Spend(
        category = "Спорт",
        description = "Фигурное катание",
        amount = 2000.50,
        currency = RUB
      ),
    }
  )
  @ApiLogin
  @Test
  void queryStat_whenSeveralSpendsWithArchivedCategory_ReturnStat(@Token String bearerToken, UserJson user) {
    StatQuery.Stat stat = getStatQueryResponseData(bearerToken, null, null, null);

    List<StatQuery.StatByCategory> expectedListStat = convertSpendsToStat(user.testData().spendings(), RUB);
    List<StatQuery.StatByCategory> factListStat = stat.statByCategories;

    Assertions.assertEquals(getSpendsSum(user.testData().spendings(), RUB), stat.total);
    Assertions.assertEquals(RUB.name(), stat.currency.rawValue);
    Assertions.assertEquals(expectedListStat, factListStat);
  }

  @User(
    spendings = {
      @Spend(
        category = "Учеба",
        description = "Обучение Niffler NG",
        amount = 10000.00,
        currency = RUB
      ),
      @Spend(
        category = "Subscription",
        description = "IntelliJ IDEA",
        amount = 150.00,
        currency = EUR
      )
    }
  )
  @ApiLogin
  @Test
  void queryStat_whenSeveralCurrencies_ReturnStatInRub(@Token String bearerToken, UserJson user) {
    StatQuery.Stat stat = getStatQueryResponseData(bearerToken, null, null, null);

    List<StatQuery.StatByCategory> expectedListStat =
      convertSpendsToStat(user.testData().spendings(), RUB);
    List<StatQuery.StatByCategory> factListStat = stat.statByCategories;

    Assertions.assertEquals(getSpendsSum(user.testData().spendings(), RUB), stat.total);
    Assertions.assertEquals(RUB.name(), stat.currency.rawValue);
    Assertions.assertEquals(expectedListStat, factListStat);
  }

  @User(
    spendings = {
      @Spend(
        category = "Учеба",
        description = "Обучение Niffler NG",
        amount = 10000.00,
        currency = RUB
      ),
      @Spend(
        category = "Subscription",
        description = "IntelliJ IDEA",
        amount = 150.00,
        currency = EUR
      )
    }
  )
  @ApiLogin
  @Test
  void queryStat_whenSeveralCurrenciesWithFilter_ReturnFilteredStat(@Token String bearerToken, UserJson user) {
    StatQuery.Stat stat = getStatQueryResponseData(
      bearerToken,
      quru.qa.type.CurrencyValues.EUR,
      quru.qa.type.CurrencyValues.EUR,
      null);

    List<SpendJson> filteredSpends = filterSpendsByCurrency(user.testData().spendings(), EUR);
    List<StatQuery.StatByCategory> expectedListStat =
      convertSpendsToStat(filteredSpends, EUR);
    List<StatQuery.StatByCategory> factListStat = stat.statByCategories;

    Assertions.assertEquals(getSpendsSum(filteredSpends, EUR), stat.total);
    Assertions.assertEquals(EUR.name(), stat.currency.rawValue);
    Assertions.assertEquals(expectedListStat, factListStat);
  }


  private List<StatQuery.StatByCategory> convertSpendsToStat(List<SpendJson> list, CurrencyValues currency) {
    Map<String, List<SpendJson>> grouped = groupByCategoryName(list);

    List<StatQuery.StatByCategory> statList = grouped.entrySet().stream()
      .map(el -> {
          String categoryName = el.getKey();
          Double sum = getSpendsSum(el.getValue(), currency);
          Date firstSpendDate = el.getValue().stream().map(SpendJson::spendDate).min(Date::compareTo).orElseThrow();
          Date lastSpendDate = el.getValue().stream().map(SpendJson::spendDate).max(Date::compareTo).orElseThrow();

          return new StatQuery.StatByCategory(
            categoryName,
            quru.qa.type.CurrencyValues.safeValueOf(currency.name()),
            sum,
            firstSpendDate,
            lastSpendDate);
        }
      )
      .toList();
    return statList;
  }

  private Map<String, List<SpendJson>> groupByCategoryName(List<SpendJson> list) {
    return list.stream()
      .sorted(Comparator.comparing(x -> x.category().name().toLowerCase()))
      .collect(Collectors.groupingBy(
          spend -> spend.category().archived()
            ? "Archived"
            : spend.category().name()
        )
      );
  }

  private List<SpendJson> filterSpendsByCurrency(List<SpendJson> list, CurrencyValues currency) {
    return list.stream()
      .filter(spend -> spend.currency().equals(currency))
      .toList();
  }

  private Double getSpendsSum(List<SpendJson> list, CurrencyValues currency) {
    return list.stream().mapToDouble(
        spend ->
          RUB.equals(currency)
            ? spend.amount() * spend.currency().tuRub
            : spend.amount()
      )
      .sum();
  }

  private StatQuery.Stat getStatQueryResponseData(String bearerToken,
                                                  quru.qa.type.CurrencyValues filterCurrency,
                                                  quru.qa.type.CurrencyValues statCurrency,
                                                  FilterPeriod filterPeriod) {
    ApolloCall<StatQuery.Data> statCall = apolloClient.query(
      StatQuery.builder()
        .filterCurrency(filterCurrency)
        .statCurrency(statCurrency)
        .filterPeriod(filterPeriod)
        .build()
    ).addHttpHeader("authorization", bearerToken);

    ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();
    final StatQuery.Data data = response.dataOrThrow();
    return data.stat;
  }
}
