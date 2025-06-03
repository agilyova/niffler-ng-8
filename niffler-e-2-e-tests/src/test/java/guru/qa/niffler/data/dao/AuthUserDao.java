package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserDao {

  @Nonnull
  AuthUserEntity create(AuthUserEntity entity);

  @Nonnull
  AuthUserEntity update(AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(UUID id);

  @Nonnull
  List<AuthUserEntity> findAll();

  void delete(AuthUserEntity entity);

}