package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import java.util.Optional;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();
//  private final SpendRepository spendRepository = new SpendRepositorySpringJdbc();
//  private final SpendRepository spendRepository = new SpendRepositoryJdbc();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
    CFG.spendJdbcUrl()
  );

  public SpendJson create(SpendJson spend) {
    return xaTransactionTemplate.execute(() -> {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        CategoryEntity categoryEntity = spendEntity.getCategory();
        if (categoryEntity.getId() == null) {
          String username = categoryEntity.getUsername();
          String name = categoryEntity.getName();

          Optional<CategoryEntity> existingCategoryOpt = spendRepository.findCategoryByUsernameAndCategoryName(username, name);
          existingCategoryOpt.ifPresent(spendEntity::setCategory);
        }

        return SpendJson.fromEntity(spendRepository.create(spendEntity));
      }
    );
  }

  public void remove(SpendJson spend) {
    xaTransactionTemplate.execute(() -> {
        if (spend.id() == null) {
          throw new IllegalArgumentException("Spend id must be present");
        }
        spendRepository.remove(SpendEntity.fromJson(spend));
        return null;
      }
    );
  }
}
