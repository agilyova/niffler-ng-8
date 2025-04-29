package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userData.FriendshipStatus;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.data.jpa.EntityManegers;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

  private static final Config CFG = Config.getInstance();
  private final EntityManager em = EntityManegers.em(CFG.userdataJdbcUrl());

  @Override
  public UserEntity create(UserEntity user) {
    em.joinTransaction();
    em.persist(user);
    return user;
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    return Optional.ofNullable(em.find(UserEntity.class, id));
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try {
      return Optional.of(em.createQuery(
          "SELECT u FROM UserEntity u WHERE u.username =: username", UserEntity.class)
        .setParameter("username", username)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public UserEntity update(UserEntity user) {
    em.joinTransaction();
    em.merge(user);
    return user;
  }

  @Override
  public void addInvitation(UserEntity requester, UserEntity addressee) {
    em.joinTransaction();
    //см. реализацию addInvitations в UserEntity → fe.setAddressee(this);
    addressee.addInvitations(requester);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    em.joinTransaction();
    requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
    addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
  }

  @Override
  public void remove(UserEntity user) {
    em.joinTransaction();
    UserEntity userEntity = em.find(UserEntity.class, user.getId());
    em.remove(userEntity);
  }
}
