package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendExtension.class);
  private final SpendClient spendClient = new SpendDbClient();
//  private final SpendClient spendClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
      .ifPresent(
        userAnnotation -> {
          if (userAnnotation.spendings().length > 0) {
            UserJson createdUser = UserExtension.createdUser();
            final String username = createdUser != null
              ? createdUser.username()
              : userAnnotation.userName();

            final List<SpendJson> createdSpendings = new ArrayList<>();
            for (Spend spend : userAnnotation.spendings()) {
              SpendJson spendJson = new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                  null,
                  spend.category(),
                  username,
                  false
                ),
                spend.currency(),
                spend.amount(),
                spend.description(),
                username
              );
              createdSpendings.add(
                spendClient.createSpend(spendJson)
              );
            }

            if (createdUser != null) {
              createdUser.testData().spendings().addAll(createdSpendings);
            }
            context.getStore(NAMESPACE).put(context.getUniqueId(), createdSpendings);

          }
        }
      );
  }

  @Override
  public void afterEach(ExtensionContext context) {
    List<SpendJson> spendings = context.getStore(NAMESPACE).get(context.getUniqueId(), List.class);
    if (spendings != null && !spendings.isEmpty()) {
      try {
        spendings.forEach(spendClient::removeSpend);
        spendings.forEach(
          spendJson -> {
            if (spendClient.findSpendingsByCategory(spendJson.category()).isEmpty()) {
              spendClient.removeCategory(spendJson.category());
            }
          }
        );
      } catch (Exception e) {
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (SpendJson[]) extensionContext.getStore(NAMESPACE)
      .get(extensionContext.getUniqueId(), List.class)
      .stream()
      .toArray(SpendJson[]::new);
  }
}
