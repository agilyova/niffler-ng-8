package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userData.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserDAO {

  @Nonnull
  UserEntity create(UserEntity user);

  @Nonnull
  Optional<UserEntity> findById(UUID id);

  @Nonnull
  Optional<UserEntity> findByUsername(String username);

  @Nonnull
  List<UserEntity> findAll();

  void delete(UserEntity user);
}
