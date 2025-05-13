package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

  private static final String caseName = "Niffler backends logs";
  private final List<String> serviceList = List.of(
    "niffler-auth",
    "niffler-currency",
    "niffler-gateway",
    "niffler-spend",
    "niffler-userdata"
  );

  @Override
  public void afterSuite() {
    final String caseId = UUID.randomUUID().toString();

    AllureLifecycle allureLifecycle = Allure.getLifecycle();
    allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
    allureLifecycle.startTestCase(caseId);

    serviceList.forEach(serviceName -> {
      try {
        allureLifecycle.addAttachment(
          serviceName,
          "text/html",
          ".log",
          Files.newInputStream(
            Path.of("./logs/%s/app.log".formatted(serviceName))
          )
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    allureLifecycle.stopTestCase(caseId);
    allureLifecycle.writeTestCase(caseId);
  }
}
