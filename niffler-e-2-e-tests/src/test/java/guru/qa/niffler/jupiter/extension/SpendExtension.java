package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.CategoryDbClient;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendExtension.class);
  private final SpendDbClient spendDbClient = new SpendDbClient();
  private final CategoryDbClient categoryDbClient = new CategoryDbClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
      .ifPresent(
        userAnnotation -> {
          if (userAnnotation.spendings().length > 0) {
            Spend firstSpend = userAnnotation.spendings()[0];
            SpendJson spendJson = new SpendJson(
              null,
              new Date(),
              new CategoryJson(
                null,
                firstSpend.category(),
                userAnnotation.userName(),
                false
              ),
              firstSpend.currency(),
              firstSpend.amount(),
              firstSpend.description(),
              userAnnotation.userName()
            );

            SpendJson created = spendDbClient.create(spendJson);
            context.getStore(NAMESPACE).put(context.getUniqueId(), created);
          }
        }
      );
  }

  @Override
  public void afterEach(ExtensionContext context) {
    SpendJson spend = context.getStore(NAMESPACE).get(context.getUniqueId(), SpendJson.class);
    if (spend != null) {
      spendDbClient.delete(spend);
      categoryDbClient.deleteCategoryIfSpendsAbsent(spend.category());
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
  }

  @Override
  public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
  }
}
