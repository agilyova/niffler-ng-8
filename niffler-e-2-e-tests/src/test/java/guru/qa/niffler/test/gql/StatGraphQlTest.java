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
import quru.qa.StatQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatGraphQlTest extends BaseGraphQlTest {

  @User
  @ApiLogin
  @Test
  void queryStat_whenNoData_ReturnZero(@Token String bearerToken) {
    ApolloCall<StatQuery.Data> statCall = apolloClient.query(
      StatQuery.builder()
        .filterCurrency(null)
        .statCurrency(null)
        .filterPeriod(null)
        .build()
    ).addHttpHeader("authorization", bearerToken);

    ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();
    final StatQuery.Data data = response.dataOrThrow();
    StatQuery.Stat stat = data.stat;

    Assertions.assertEquals(0.0, stat.total);
  }
}
