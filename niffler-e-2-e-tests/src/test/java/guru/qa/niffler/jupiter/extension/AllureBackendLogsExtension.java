package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

  private static final String caseName = "Niffler backends logs";

  @SneakyThrows
  @Override
  public void afterSuite() {
    final String caseId = UUID.randomUUID().toString();
    AllureLifecycle allureLifecycle = Allure.getLifecycle();
    allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
    allureLifecycle.startTestCase(caseId);

    allureLifecycle.addAttachment(
      "niffler_auth.log",
      "text/html",
      ".log",
      Files.newInputStream(
        Path.of("./logs/niffler-auth/app.log")
      )
    );

    allureLifecycle.stopTestCase(caseId);
    allureLifecycle.writeTestCase(caseId);
  }
}
