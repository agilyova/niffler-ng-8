package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.spring.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {
  private static final Config CFG = Config.getInstance();
  AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

  @Override
  public AuthUserEntity create(AuthUserEntity entity) {
    AuthUserEntity createdUserEntity = authUserDao.create(entity);
    authAuthorityDao.create(entity.getAuthorities().toArray(new AuthorityEntity[0]));
    return createdUserEntity;
  }

  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    authUserDao.update(user);
    return user;
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    try {
      return Optional.ofNullable(jdbcTemplate.query(
        "SELECT u.id, " +
          "       u.username, " +
          "       u.password, " +
          "       u.enabled, " +
          "       u.account_non_expired, " +
          "       u.account_non_locked, " +
          "       u.credentials_non_expired, " +
          "       a.id as auth_id, " +
          "       authority " +
          "FROM \"user\" u join authority a on u.id = a.user_id WHERE u.id = ?",
        AuthUserEntityExtractor.instance,
        id));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String userName) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    try {
      return Optional.ofNullable(jdbcTemplate.query(
        "SELECT u.id, " +
          "       u.username, " +
          "       u.password, " +
          "       u.enabled, " +
          "       u.account_non_expired, " +
          "       u.account_non_locked, " +
          "       u.credentials_non_expired, " +
          "       a.id as auth_id, " +
          "       authority " +
          "FROM \"user\" u join authority a on u.id = a.user_id WHERE u.username = ?",
        AuthUserEntityExtractor.instance,
        userName));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void remove(AuthUserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    jdbcTemplate.update(
      "WITH deleted_authority AS " +
        "(DELETE FROM authority WHERE user_id = ?) " +
        "DELETE FROM \"user\" WHERE id = ?",
      user.getId(), user.getId()
    );
  }
}
