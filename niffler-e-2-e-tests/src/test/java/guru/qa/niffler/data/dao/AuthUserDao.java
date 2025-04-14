package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;

public interface AuthUserDao {
  AuthUserEntity create(AuthUserEntity entity);

  void delete(AuthUserEntity entity);

}
