package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;

public class UserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
  private static final String DEFAULT_PASSWORD = "12345";

  private final UsersClient usersClient = new UsersApiClient();

  private ThreadLocal<Boolean> userAlreadyExist = new ThreadLocal<>();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
      .ifPresent(
        userAnnotation -> {
          UserJson user;
          if ("".equals(userAnnotation.userName())) {
            userAlreadyExist.set(false);
            user = usersClient.createUser(
              RandomDataUtils.randomUserName(),
              DEFAULT_PASSWORD
            ).withTestData(
              new TestData(DEFAULT_PASSWORD)
            );
          } else {
            user = usersClient.findUserByUsername(userAnnotation.userName());
            userAlreadyExist.set(true);
          }
          if (userAnnotation.friends() > 0) {
            usersClient.createFriends(user, userAnnotation.friends());
          }
          if (userAnnotation.outcomeInvitations() > 0) {
            usersClient.createOutcomeInvitations(user, userAnnotation.outcomeInvitations());
          }
          if (userAnnotation.incomeInvitations() > 0) {
            usersClient.createIncomeInvitations(user, userAnnotation.incomeInvitations());
          }

          setUser(user.withPassword(DEFAULT_PASSWORD));
/*          context.getStore(NAMESPACE).put(
            context.getUniqueId(),
            user.withPassword(DEFAULT_PASSWORD)
          );*/
        }
      );
  }

  //TODO удалять созданных друзей тоже
  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    UserJson user = context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);

    if (userAlreadyExist != null && userAlreadyExist.get() != null && !userAlreadyExist.get() && user != null) {
      try {
        usersClient.remove(user);
      } catch (Exception e) {
      }
    }
    userAlreadyExist.remove();
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return getUser();
  }

  public static void setUser(UserJson user) {
    final ExtensionContext context = TestMethodContextExtension.getContext();
    context.getStore(NAMESPACE).put(context.getUniqueId(), user);
  }

  public static @Nullable UserJson getUser() {
    final ExtensionContext context = TestMethodContextExtension.getContext();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
  }
}
