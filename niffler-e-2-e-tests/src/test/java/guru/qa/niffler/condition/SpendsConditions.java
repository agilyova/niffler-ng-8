package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static java.lang.System.lineSeparator;

public class SpendsConditions {
  public static WebElementsCondition spends(SpendJson... expectedSpends) {
    return new WebElementsCondition() {

      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedSpends)) {
          throw new IllegalArgumentException("No expected spends given");
        }

        if (expectedSpends.length != elements.size()) {
          String message = String.format(
            "List size mismatch (expected: %s, actual: %s)", expectedSpends.length, elements.size());
          return rejected(message, elements);
        }

        for (int i = 0; i < elements.size(); i++) {
          List<WebElement> cells = elements.get(i).findElements(By.tagName("td"));
          SpendJson expectedSpend = expectedSpends[i];
          Map<String, String> actualMap = mapActual(cells);
          Map<String, String> expectedMap = mapExpected(expectedSpend);

          for (String key : actualMap.keySet()) {
            if (!actualMap.get(key).equals(expectedMap.get(key))) {
              String message = String.format(
                "Spend " + key + " mismatch " +
                  lineSeparator() + "Expected: %s, " +
                  lineSeparator() + "Actual: %s",
                expectedMap.get(key), actualMap.get(key)
              );
              return rejected(message, actualMap.get(key));
            }
          }
        }
        return accepted();
      }

      @Override
      public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {
        String message = lastCheckResult.message();
        throw new AssertionError(
          "Collection check failed: " + (message != null ? message : "unknown reason") +
            (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
            lineSeparator() + "Collection: " + collection.description()
        );
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedSpends);
      }
    };
  }

  private static Map<String, String> mapActual(List<WebElement> cells) {
    Map<String, String> actual = new HashMap<>();

    actual.put("category", cells.get(1).getText());
    actual.put("amount", cells.get(2).getText());
    actual.put("description", cells.get(3).getText());
    actual.put("date", cells.get(4).getText());

    return actual;
  }

  private static Map<String, String> mapExpected(SpendJson expectedSpend) {
    Map<String, String> expected = new HashMap<>();
    DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
    SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

    expected.put("category", expectedSpend.category().name());
    expected.put("amount", String.format("%s %s", df.format(expectedSpend.amount()), expectedSpend.currency().alias));
    expected.put("description", expectedSpend.description());
    expected.put("date", sdf.format(expectedSpend.spendDate()));

    return expected;
  }
}
