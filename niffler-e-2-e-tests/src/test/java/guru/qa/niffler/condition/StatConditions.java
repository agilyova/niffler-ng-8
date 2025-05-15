package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

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
}
