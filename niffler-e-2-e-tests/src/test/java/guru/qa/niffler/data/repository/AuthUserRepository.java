package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {
  AuthUserEntity create(AuthUserEntity entity);

  Optional<AuthUserEntity> findById(UUID id);

  Optional<AuthUserEntity> findByUsername(String userName);
}
