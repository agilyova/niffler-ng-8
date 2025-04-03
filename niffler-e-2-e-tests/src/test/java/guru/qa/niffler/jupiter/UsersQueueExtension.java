package guru.qa.niffler.jupiter;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.apache.commons.lang3.time.StopWatch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.PARAMETER)
  public @interface UserType {
    boolean empty() default true;
  }

  public record StaticUser(String userName, String passWord, boolean empty) {
  }

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> NOT_EMPTY_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("bee", "12345", true));
    NOT_EMPTY_USERS.add(new StaticUser("duck", "12345", false));
    NOT_EMPTY_USERS.add(new StaticUser("dima", "12345", false));
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    Arrays.stream(context.getRequiredTestMethod().getParameters())
      .filter(param -> AnnotationSupport.isAnnotated(param, UserType.class))
      .filter(param -> param.getType().equals(StaticUser.class))
      .findFirst()
      .map(param -> param.getAnnotation(UserType.class))
      .ifPresent(
        ut -> {
          Optional<StaticUser> user = Optional.empty();
          StopWatch sw = StopWatch.createStarted();
          while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
            user = ut.empty()
              ? Optional.ofNullable(EMPTY_USERS.poll())
              : Optional.ofNullable(NOT_EMPTY_USERS.poll());
          }
          Allure.getLifecycle().updateTestCase(testCase ->
            testCase.setStart(new Date().getTime()));

          user.ifPresentOrElse(
            u -> context.getStore(NAMESPACE).put(context.getUniqueId(), u),
            () -> {
              throw new IllegalStateException("Can`t obtain user after 30s.");
            }
          );
        }
      );
  }

  @Override
  public void afterEach(ExtensionContext context) {
    StaticUser user = context.getStore(NAMESPACE).get(
      context.getUniqueId(),
      StaticUser.class
    );
    if (user.empty()) {
      EMPTY_USERS.add(user);
    } else {
      NOT_EMPTY_USERS.add(user);
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
      && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), StaticUser.class);
  }
}
