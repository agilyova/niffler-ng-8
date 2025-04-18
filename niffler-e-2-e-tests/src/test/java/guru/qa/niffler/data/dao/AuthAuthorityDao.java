package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;

import java.util.List;

public interface AuthAuthorityDao {
  void create(AuthorityEntity entity);

  List<AuthorityEntity> findAll();

  void delete(AuthorityEntity entity);
}
