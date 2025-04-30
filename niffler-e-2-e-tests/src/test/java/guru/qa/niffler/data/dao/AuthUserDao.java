package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {

  AuthUserEntity create(AuthUserEntity entity);

  AuthUserEntity update(AuthUserEntity user);

  Optional<AuthUserEntity> findById(UUID id);

  List<AuthUserEntity> findAll();

  void delete(AuthUserEntity entity);

}
