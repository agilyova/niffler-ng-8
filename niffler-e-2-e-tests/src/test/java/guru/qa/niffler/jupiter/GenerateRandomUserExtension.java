package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class GenerateRandomUserExtension implements BeforeEachCallback, ParameterResolver {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(GenerateRandomUserExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), RandomUser.class)
      .ifPresent(anno -> {
        Faker faker = new Faker();
        UserJson randomUser = new UserJson(
          faker.name().username(),
          faker.internet().password(3,12)
        );
        context.getStore(NAMESPACE).put(context.getUniqueId(), randomUser);
      });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
  }
}
