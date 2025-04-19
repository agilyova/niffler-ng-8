package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;

public class CategoryDbClient {

  private static final Config CFG = Config.getInstance();

  private final CategoryDao categoryDao = new CategoryDaoJdbc();
  private final SpendDao spendDao = new SpendDaoJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
    CFG.spendJdbcUrl()
  );

  public CategoryJson createCategory(CategoryJson categoryJson) {
    return jdbcTxTemplate.execute(
      () -> {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        return CategoryJson.fromEntity(
          categoryDao.create(categoryEntity));
      }
    );
  }

  public void deleteCategoryIfSpendsAbsent(CategoryJson categoryJson) {
    jdbcTxTemplate.execute(() -> {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        if (spendDao.findByCategory(categoryEntity).isEmpty()) {
          categoryDao.delete(categoryEntity);
        }
        return null;
      }
    );
  }
}
