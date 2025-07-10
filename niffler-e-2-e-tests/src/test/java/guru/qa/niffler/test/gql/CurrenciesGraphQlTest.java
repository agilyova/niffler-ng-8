package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.enums.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import quru.qa.CurrenciesQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrenciesGraphQlTest extends BaseGraphQlTest {


  @User
  @ApiLogin
  @Test
  void allCurrenciesShouldReturnFromGateway(@Token String bearerToken) {
    ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient.query(
      new CurrenciesQuery()
    ).addHttpHeader("authorization", bearerToken);

    ApolloResponse<CurrenciesQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
    final CurrenciesQuery.Data data = response.dataOrThrow();
    List<CurrenciesQuery.Currency> currencies = data.currencies;
    Set<String> actualValues = Arrays.stream(CurrencyValues.values())
      .map(c -> c.name())
      .collect(Collectors.toSet());
    Set<String> factList = currencies.stream().map(c -> c.currency.rawValue).collect(Collectors.toSet());

    Assertions.assertEquals(CurrencyValues.values().length, currencies.size());
    Assertions.assertEquals(actualValues, factList);
  }
}
