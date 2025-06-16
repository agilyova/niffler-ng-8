package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CookiesExtension implements AfterEachCallback {
  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    ThreadSafeCookieStore.INSTANCE.removeAll();
  }
}
