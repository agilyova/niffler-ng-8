package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userData.UserEntity;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserdataUserDAO {

  UserEntity createUser(UserEntity user);

  Optional<UserEntity> findUserById(UUID id);

  Optional<UserEntity> findByUsername(String username);

  List<UserEntity> findAll();

  void delete(UserEntity user);
}
