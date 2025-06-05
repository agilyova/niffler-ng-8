package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.chrome.ChromeOptions;

public class ConfigurationExtension implements BeforeAllCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    ChromeOptions options = new ChromeOptions();
    options.addArguments(
      "--no-sandbox",
      "--disable-dev-shm-usage",
      "--disable-gpu", // Intel XE problems with parallel execution
      "--disable-extensions",
      "--remote-allow-origins=*"
    );
    Configuration.browser = "chrome";
    Configuration.browserSize="1920x1080";
    Configuration.browserCapabilities = options;
  }
}
