package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Optional;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final SpendRepository spendRepository = new SpendRepositoryHibernate();
//  private final SpendRepository spendRepository = new SpendRepositorySpringJdbc();
//  private final SpendRepository spendRepository = new SpendRepositoryJdbc();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
    CFG.spendJdbcUrl()
  );

  @Step("Create spend \"{0.description}\" with DB")
  public SpendJson createSpend(SpendJson spend) {
    return xaTransactionTemplate.execute(() -> {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        CategoryEntity categoryEntity = spendEntity.getCategory();
        if (categoryEntity.getId() == null) {
          String username = categoryEntity.getUsername();
          String name = categoryEntity.getName();

          Optional<CategoryEntity> existingCategoryOpt = spendRepository.findCategoryByUsernameAndCategoryName(username, name);
          existingCategoryOpt.ifPresent(spendEntity::setCategory);
        }

        return SpendJson.fromEntity(spendRepository.createSpend(spendEntity));
      }
    );
  }

  @Step("Create category \"{0.name}\" with DB")
  @Override
  public CategoryJson createCategory(CategoryJson category) {
    return xaTransactionTemplate.execute(
      () -> CategoryJson.fromEntity(
        spendRepository.createCategory(CategoryEntity.fromJson(category)
        )
      )
    );
  }

  @Step("Find spendings by category \"{0.name}\" with DB")
  @Override
  public List<SpendJson> findSpendingsByCategory(CategoryJson categoryJson) {
    List<SpendEntity> spendEntities = spendRepository.findSpendingsByCategory(CategoryEntity.fromJson(categoryJson));
    return spendEntities.stream().map(SpendJson::fromEntity).toList();
  }

  @Step("Delete spending \"{0.description}\" with DB")
  public void removeSpend(SpendJson spend) {
    xaTransactionTemplate.execute(() -> {
        if (spend.id() == null) {
          throw new IllegalArgumentException("Spend id must be present");
        }
        spendRepository.remove(SpendEntity.fromJson(spend));
        return null;
      }
    );
  }

  @Step("Delete category \"{0.description}\"   with DB")
  public void removeCategory(CategoryJson category) {
    xaTransactionTemplate.execute(() -> {
        if (category.id() == null) {
          throw new IllegalArgumentException("Category id must be present");
        }
        spendRepository.removeCategory(CategoryEntity.fromJson(category));
        return null;
      }
    );
  }
}
