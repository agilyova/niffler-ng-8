package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import static guru.qa.niffler.data.DataBases.transaction;

public class CategoryDbClient {

  private static final Config CFG = Config.getInstance();

  public CategoryJson createCategory(CategoryJson categoryJson) {
    return transaction(
      connection -> {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        return CategoryJson.fromEntity(
          new CategoryDaoJdbc(connection).create(categoryEntity));
      }
      , CFG.spendJdbcUrl());
  }

  public void deleteCategoryIfSpendsAbsent(CategoryJson categoryJson) {
    transaction(connection -> {
      CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
      if (new SpendDaoJdbc(connection).findSpendsByCategory(categoryEntity).isEmpty()) {
        new CategoryDaoJdbc(connection).deleteCategory(categoryEntity);
      }
    }, CFG.spendJdbcUrl());
  }
}
