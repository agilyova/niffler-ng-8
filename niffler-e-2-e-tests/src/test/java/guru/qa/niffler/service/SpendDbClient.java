package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;

import static guru.qa.niffler.data.DataBases.transaction;

public class SpendDbClient {

  private static final Config CFG = Config.getInstance();

  public SpendJson createSpend(SpendJson spend) {
    return transaction(connection -> {
        CategoryDao categoryDao = new CategoryDaoJdbc(connection);
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
          CategoryEntity categoryEntity = categoryDao
            .findCategoryByUsernameAndCategoryName(spend.username(), spend.category().name())
            .orElseGet(() -> categoryDao.create(spendEntity.getCategory()));
          spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(new SpendDaoJdbc(connection).create(spendEntity));
      },
      CFG.spendJdbcUrl());
  }

  public void deleteSpend(SpendJson spend) {
    transaction(connection -> {
      new SpendDaoJdbc(connection).deleteSpend(SpendEntity.fromJson(spend));
    }, CFG.spendJdbcUrl());
  }
}
