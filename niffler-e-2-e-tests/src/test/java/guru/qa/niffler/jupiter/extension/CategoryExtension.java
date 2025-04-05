package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {

    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
      .ifPresent(
        userAnnotation -> {
          if (userAnnotation.categories().length > 0) {
            Category firstCategory = userAnnotation.categories()[0];
            CategoryJson categoryJson = new CategoryJson(
              null,
              RandomDataUtils.randomCategoryName(),
              userAnnotation.userName(),
              false);
            CategoryJson createdCategory = spendApiClient.addCategory(categoryJson);
            context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);

            if (firstCategory.archived()) {
              CategoryJson archivedCategory = new CategoryJson(
                createdCategory.id(),
                createdCategory.name(),
                createdCategory.username(),
                true);
              createdCategory = spendApiClient.updateCategory(archivedCategory);
              context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);
            }
          }
        }
      );
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    CategoryJson createdCategory = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);

    if (createdCategory != null) {
      spendApiClient.updateCategory(new CategoryJson(
        createdCategory.id(),
        createdCategory.name(),
        createdCategory.username(),
        true
      ));
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
  }
}
