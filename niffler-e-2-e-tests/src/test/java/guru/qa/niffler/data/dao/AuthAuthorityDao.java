package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;

import java.util.List;

public interface AuthAuthorityDao {
  void create(AuthorityEntity entity);

  void create(AuthorityEntity... authority);

  List<AuthorityEntity> findAll();

  void delete(AuthorityEntity entity);
}
