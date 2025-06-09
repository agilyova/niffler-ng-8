package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Deprecated
public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.PARAMETER)
  public @interface UserType {
    Type value() default Type.EMPTY;

    enum Type {
      EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
    }
  }

  public record StaticUser(
    String userName,
    String password,
    String friend,
    String income,
    String outcome) {
  }

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("emptyUser", "123456", null, null, null));
    WITH_FRIEND_USERS.add(new StaticUser("with_friend", "123456", "admin", null, null));
    WITH_INCOME_REQUEST_USERS.add(new StaticUser("with_income", "123456", null, "with_outcome", null));
    WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("with_outcome", "123456", null, null, "with_income"));
  }

  @Override
  @SuppressWarnings("unchecked")
  public void beforeEach(ExtensionContext context) {
    Arrays.stream(context.getRequiredTestMethod().getParameters())
      .filter(param -> AnnotationSupport.isAnnotated(param, UserType.class))
      .filter(param -> param.getType().equals(StaticUser.class))
      .map(param -> param.getAnnotation(UserType.class))
      .forEach(
        ut -> {
          Optional<StaticUser> user = Optional.empty();
          StopWatch sw = StopWatch.createStarted();
          while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 10) {
            user = Optional.ofNullable(getQueueByUserType(ut).poll());
          }
          Allure.getLifecycle().updateTestCase(testCase ->
            testCase.setStart(new Date().getTime()));
          user.ifPresentOrElse(
            u -> ((Map<UserType, StaticUser>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
              context.getUniqueId(),
              key -> new HashMap<>()
            )).put(ut, u),
            () -> {
              throw new IllegalStateException("Can`t obtain user after 30s.");
            }
          );
        }
      );
  }

  @Override
  @SuppressWarnings("unchecked")
  public void afterEach(ExtensionContext context) {
    Map<UserType, StaticUser> users = context.getStore(NAMESPACE).get(
      context.getUniqueId(),
      Map.class
    );
    if (users != null) {
      for (Map.Entry<UserType, StaticUser> e : users.entrySet()) {
        getQueueByUserType(e.getKey()).add(e.getValue());
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
      && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class)
      .get(
        AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class).get()
      );
  }

  private Queue<StaticUser> getQueueByUserType(UserType userType) {
    return switch (userType.value()) {
      case EMPTY -> EMPTY_USERS;
      case WITH_FRIEND -> WITH_FRIEND_USERS;
      case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS;
      case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS;
    };
  }
}
