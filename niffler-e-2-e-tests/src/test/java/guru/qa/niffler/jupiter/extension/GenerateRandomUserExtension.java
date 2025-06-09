package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.RandomUser;
import guru.qa.niffler.model.UserJ;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

@Deprecated
public class GenerateRandomUserExtension implements BeforeEachCallback, ParameterResolver {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(GenerateRandomUserExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), RandomUser.class)
      .ifPresent(anno -> {
        UserJ randomUser = new UserJ(
          RandomDataUtils.randomUserName(),
          RandomDataUtils.randomPassword(3, 12)
        );
        context.getStore(NAMESPACE).put(context.getUniqueId(), randomUser);
      });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJ.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJ.class);
  }
}
