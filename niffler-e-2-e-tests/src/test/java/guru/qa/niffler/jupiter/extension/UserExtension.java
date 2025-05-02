package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;

public class UserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

  private final UsersClient usersClient = new UsersDbClient();
  private final SpendClient spendClient = new SpendDbClient();

  private static final String DEFAULT_PASSWORD = "12345";

  @Override
  public void beforeEach(ExtensionContext context) {

    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
      .ifPresent(
        userAnnotation -> {
          if ("".equals(userAnnotation.userName())) {
            UserJson user = usersClient.createUser(
              RandomDataUtils.randomUserName(),
              DEFAULT_PASSWORD
            );
            context.getStore(NAMESPACE).put(
              context.getUniqueId(),
              user.withPassword(DEFAULT_PASSWORD)
            );
          }
        }
      );
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    UserJson user = context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);

    if (user != null) {
      usersClient.remove(user);
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdUser();
  }

  public static @Nullable UserJson createdUser() {
    final ExtensionContext context  = TestMethodContextExtension.getContext();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
  }
}
