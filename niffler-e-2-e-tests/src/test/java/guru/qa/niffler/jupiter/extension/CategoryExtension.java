package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendClient spendClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) {

    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
      .ifPresent(
        userAnnotation -> {
          if (userAnnotation.categories().length > 0) {
            UserJson createdUser = UserExtension.getUser();
            final String username = createdUser != null
              ? createdUser.username()
              : userAnnotation.userName();

            final List<CategoryJson> createdCategories = new ArrayList<>();

            for (Category category : userAnnotation.categories()) {
              final String categoryName = "".equals(category.name())
                ? RandomDataUtils.randomCategoryName()
                : category.name();

              CategoryJson categoryJson = new CategoryJson(
                null,
                categoryName,
                username,
                category.archived()
              );
              createdCategories.add(
                spendClient.createCategory(categoryJson)
              );
            }

            if (createdUser != null) {
              createdUser.testData().categories().addAll(createdCategories);
            } else {
              context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategories);
            }
          }
        }
      );
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    List<CategoryJson> createdCategories = context.getStore(NAMESPACE).get(context.getUniqueId(), List.class);

    if (createdCategories != null && !createdCategories.isEmpty()) {
      createdCategories.forEach(spendClient::removeCategory);
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
  }


  @Override
  @SuppressWarnings("unchecked")
  public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (CategoryJson[]) extensionContext.getStore(NAMESPACE)
      .get(extensionContext.getUniqueId(), List.class)
      .stream()
      .toArray(CategoryJson[]::new);
  }
}
