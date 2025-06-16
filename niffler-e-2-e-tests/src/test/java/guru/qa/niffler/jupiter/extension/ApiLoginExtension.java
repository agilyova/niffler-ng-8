package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
  private static final Config CFG = Config.getInstance();

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final boolean setupBrowser;

  private ApiLoginExtension(boolean setupBrowser) {
    this.setupBrowser = setupBrowser;
  }

  private ApiLoginExtension() {
    this.setupBrowser = true;
  }

  public static ApiLoginExtension restApiLoginExtension() {
    return new ApiLoginExtension(false);
  }


  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
      .ifPresent(
        apiLogin -> {

          final UserJson userToLogin;
          final UserJson userFromUserExtension = UserExtension.getUser();
          if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {

            if (userFromUserExtension == null) {
              throw new IllegalStateException("@User must be present if @ApiLogin is empty");
            }
            userToLogin = userFromUserExtension;
          } else {
            UserJson fakeUser = new UserJson(
              apiLogin.username()).withPassword(apiLogin.password()
            );
            if (userFromUserExtension != null) {
              throw new IllegalStateException("@User mast NOT be present if @ApiLogin is NOT empty");
            }
            UserExtension.setUser(fakeUser);
            userToLogin = fakeUser;
          }

          final String token = authApiClient.loginAs(
            userToLogin.username(),
            userToLogin.testData().password()
          );
          setToken(token);
          if (setupBrowser) {
            Selenide.open(CFG.frontUrl() + "/favicon.ico");
            Selenide.localStorage().setItem("id_token", getToken());
            WebDriverRunner.getWebDriver().manage().addCookie(
              new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
              )
            );
          }
        }
      );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(String.class)
      && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
  }

  @Override
  public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return getToken();
  }

  public static void setToken(String token) {
    TestMethodContextExtension.getContext().getStore(NAMESPACE).put("token", token);
  }

  public static String getToken() {
    return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("token", String.class);
  }

  public static void setCode(String code) {
    TestMethodContextExtension.getContext().getStore(NAMESPACE).put("code", code);
  }

  public static String getCode() {
    return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("code", String.class);
  }

  public static Cookie getJsessionIdCookie() {
    return new Cookie(
      "JSESSIONID",
      ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
    );
  }
}
