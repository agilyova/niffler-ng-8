package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDiff;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);

  public static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
      parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
  }

  @SneakyThrows
  @Override
  public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return ImageIO.read(
      new ClassPathResource(
        extensionContext
          .getRequiredTestMethod()
          .getAnnotation(ScreenShotTest.class).value())
        .getInputStream());
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    if (getExpected() != null && getActual() != null && getDiff() != null) {

      ScreenShotTest annotation = context.getRequiredTestMethod().getAnnotation(ScreenShotTest.class);
      if (annotation.rewriteExpected()) {
        try {
          ImageIO.write(getActual(), "png", new File("src/test/resources/" + annotation.value()));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      ScreenDiff screenDiff = new ScreenDiff(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getExpected())),
        "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getActual())),
        "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getDiff()))
      );
      Allure.addAttachment(
        "ScreenShot diff",
        "application/vnd.allure.image.diff",
        objectMapper.writeValueAsString(screenDiff)
      );

    }
    throw throwable;
  }

  public static void setExpected(BufferedImage image) {
    TestMethodContextExtension.getContext().getStore(NAMESPACE).put("expected", image);
  }

  public static BufferedImage getExpected() {
    return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("expected", BufferedImage.class);
  }

  public static void setActual(BufferedImage image) {
    TestMethodContextExtension.getContext().getStore(NAMESPACE).put("actual", image);
  }

  public static BufferedImage getActual() {
    return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("actual", BufferedImage.class);
  }

  public static void setDiff(BufferedImage image) {
    TestMethodContextExtension.getContext().getStore(NAMESPACE).put("diff", image);
  }

  public static BufferedImage getDiff() {
    return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("diff", BufferedImage.class);
  }

  private static byte[] imageToBytes(BufferedImage image) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", outputStream);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
