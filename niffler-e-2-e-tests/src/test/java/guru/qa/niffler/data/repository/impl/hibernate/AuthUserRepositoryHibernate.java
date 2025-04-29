package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.jpa.EntityManegers;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositoryHibernate implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();
  private final EntityManager em = EntityManegers.em(CFG.authJdbcUrl());

  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    em.joinTransaction();
    em.persist(user);
    return user;
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    return Optional.ofNullable(em.find(AuthUserEntity.class, id));
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try {
      return Optional.of(em.createQuery(
          "SELECT u FROM AuthUserEntity u WHERE u.username =: username", AuthUserEntity.class)
        .setParameter("username", username)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}
