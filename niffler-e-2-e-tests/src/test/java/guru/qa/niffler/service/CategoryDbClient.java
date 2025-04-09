package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

public class CategoryDbClient {
  private final CategoryDao categoryDao = new CategoryDaoJdbc();
  private final SpendDao spendDao = new SpendDaoJdbc();

  public CategoryJson createCategory(CategoryJson categoryJson) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
    return CategoryJson.fromEntity(
      categoryDao.create(categoryEntity)
    );
  }

  public void deleteCategory(CategoryJson categoryJson) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
    if (spendDao.findSpendsByCategory(categoryEntity).isEmpty()) {
      categoryDao.deleteCategory(categoryEntity);
    }
  }
}
