package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.niffler.model.Bubble;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static guru.qa.niffler.condition.Color.fromRgba;
import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class StatConditions {

  public static WebElementCondition color(Color expectedColor) {

    return new WebElementCondition("color") {
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        final String rgba = element.getCssValue("background-color");

        return new CheckResult(
          expectedColor.rgb.equals(rgba),
          rgba
        );
      }

      @Override
      public String toString() {
        return String.format("%s=\"%s\"", getName(), expectedColor.rgb);
      }
    };
  }

  public static WebElementsCondition colors(Color... expectedColor) {

    final String expectedRgba = Arrays.stream(expectedColor).map(c -> c.rgb).toList().toString();

    return new WebElementsCondition() {

      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedColor)) {
          throw new IllegalArgumentException("No expected colors given");
        }

        if (elements.size() != expectedColor.length) {
          String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedColor.length, elements.size());
          return rejected(message, elements.stream().map(el -> el.getCssValue("background-color")).toList());
        }

        boolean passed = true;
        List<String> actualRgbaList = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
          final WebElement elementToCheck = elements.get(i);
          final Color colorToCheck = expectedColor[i];
          final String rgba = elementToCheck.getCssValue("background-color");
          actualRgbaList.add(rgba);
          if (passed) {
            passed = colorToCheck.rgb.equals(rgba);
          }
        }
        if (!passed) {
          final String actualRgba = actualRgbaList.toString();
          String message = String.format("List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba);
          return rejected(message, actualRgba);
        }
        return accepted();
      }

      @Override
      public String toString() {
        return expectedRgba;
      }
    };
  }

  public static WebElementsCondition statBubbles(Bubble... expectedBubbles) {
    return baseStatBubbles(
      Collectors.toList(),
      Objects::equals,
      expectedBubbles
    );
  }

  public static WebElementsCondition statBubblesInAnyOrder(Bubble... expectedBubbles) {
    return baseStatBubbles(
      Collectors.toSet(),
      Objects::equals,
      expectedBubbles
    );
  }

  public static WebElementsCondition statBubblesContains(Bubble... expectedBubbles) {
    return new WebElementsCondition() {

      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedBubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }

        List<Bubble> actualList = elementsToCollection(elements, Collectors.toList());
        List<Bubble> expectedList = Arrays.stream(expectedBubbles).toList();

        if (actualList.containsAll(expectedList)) {
          return accepted();
        } else {
          String message = String.format(
            "List bubbles mismatch" +
              lineSeparator() + "Expected: %s, " +
              lineSeparator() + "Actual: %s",
            expectedList, actualList);
          return rejected(message, actualList);
        }
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedBubbles);
      }
    };
  }

  private static <T extends Collection<Bubble>> T elementsToCollection(
    List<WebElement> elements,
    Collector<Bubble, ?, T> collector
  ) {
    return elements.stream()
      .map(el ->
        new Bubble(fromRgba(el.getCssValue("background-color")), el.getText()))
      .collect(collector);
  }

  private static <T extends Collection<Bubble>> WebElementsCondition baseStatBubbles(
    Collector<Bubble, ?, T> collector,
    BiPredicate<T, T> predicate,
    Bubble... expectedBubbles) {
    return new WebElementsCondition() {
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {

        if (ArrayUtils.isEmpty(expectedBubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }

        T actual = elementsToCollection(elements, collector);
        T expected = Arrays.stream(expectedBubbles).collect(collector);

        if (elements.size() != expectedBubbles.length) {
          String message = String.format(
            "List size mismatch " +
              lineSeparator() + "Expected: %s, " +
              lineSeparator() + "Actual: %s",
            expectedBubbles.length, elements.size());
          return rejected(message, actual);
        }

        if (predicate.test(expected, actual)) {
          return accepted();
        } else {
          String message = String.format(
            "List bubbles mismatch " +
              lineSeparator() + "Expected: %s, " +
              lineSeparator() + "Actual: %s",
            expected, actual);
          return rejected(message, actual);
        }
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
        return Arrays.toString(expectedBubbles);
      }
    };
  }
}
