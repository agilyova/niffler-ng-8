package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.*;

public class StatComponent {
  private final SelenideElement self = $("#stat");

  private final SelenideElement diagramElement = self.$("canvas[role = 'img']");
  private final ElementsCollection badges = self.$$("#legend-container li");
  private final SelenideElement confirmDialog = $("div[role = 'dialog']");
  private final SelenideElement submitActionButton = confirmDialog.$("button:last-of-type");

  @SneakyThrows
  @Step("Check that diagram is equal to reference image")
  public StatComponent checkDiagram(BufferedImage expected) {
    Selenide.sleep(3000);
    BufferedImage actual = ImageIO.read(diagramElement.screenshot());

    Assertions.assertFalse(
      new ScreenDiffResult(
        expected,
        actual
      )
    );
    return this;
  }

  @Step("Check that bubbles equals to expected values")
  public StatComponent checkExactBubbles(Bubble... expectedBubbles) {
    badges.shouldHave(statBubbles(expectedBubbles));
    return this;
  }

  @Step("Check that bubbles equals to expected values in any order")
  public StatComponent checkBubblesInAnyOrder(Bubble... expectedBubbles) {
    badges.shouldHave(statBubblesInAnyOrder(expectedBubbles));
    return this;
  }

  @Step("Check that bubbles contains expected values")
  public StatComponent checkContainsBubbles(Bubble... expectedBubbles) {
    badges.shouldHave(statBubblesContains(expectedBubbles));
    return this;
  }
}
