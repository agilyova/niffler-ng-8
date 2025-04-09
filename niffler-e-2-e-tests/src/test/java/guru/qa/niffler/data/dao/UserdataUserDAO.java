package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userData.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDAO {

  UserEntity createUser(UserEntity user);

  Optional<UserEntity> findUserById(UUID id);

  Optional<UserEntity> findByUsername(String username);

  void delete(UserEntity user);
}
