package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userData.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {

  UserEntity create(UserEntity user);

  Optional<UserEntity> findById(UUID id);

  void addInvitation(UserEntity requester, UserEntity addressee);

  void addFriend(UserEntity requester, UserEntity addressee);

  //В ДЗ есть этот метод, но он не имеет смысла
  //void addOutcomeInvitation(UserEntity requester, UserEntity addressee);
}
