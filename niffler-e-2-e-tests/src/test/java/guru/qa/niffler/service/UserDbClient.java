package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UserDbClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserDao authUserSpringDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthoritySpringDao = new AuthAuthorityDaoSpringJdbc();
  private final UserdataUserDAO udUserSpringDao = new UserdataUserDAOSpringJdbc();

  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
  private final UserdataUserDAO udUserDao = new UserdataUserDAOSpringJdbc();

  private final TransactionTemplate txTemplate = new TransactionTemplate(
    new JdbcTransactionManager(
      dataSource(CFG.authJdbcUrl())
    )
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
    CFG.authJdbcUrl(),
    CFG.userdataJdbcUrl()
  );

  final TransactionTemplate getTxTemplate = new TransactionTemplate(
    new ChainedTransactionManager(
      new JdbcTransactionManager(dataSource(CFG.authJdbcUrl())),
      new JdbcTransactionManager(dataSource(CFG.userdataJdbcUrl()))
    )
  );

  //Xatransaction + Spring
  public UserJson createUser(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserSpringDao.create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
          e -> {
            AuthorityEntity ae = new AuthorityEntity();
            ae.setUserId(createdAuthUser.getId());
            ae.setAuthority(e);
            return ae;
          }
        ).toArray(AuthorityEntity[]::new);

        authAuthoritySpringDao.create(authorityEntities);
        return UserJson.fromEntity(
          udUserSpringDao.create(UserEntity.fromJson(user))
        );
      }
    );
  }

  public UserJson createUserSpringWithOutTransactions(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);

    AuthUserEntity createdAuthUser = authUserSpringDao.create(authUser);

    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
      e -> {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setUserId(createdAuthUser.getId());
        ae.setAuthority(e);
        return ae;
      }
    ).toArray(AuthorityEntity[]::new);

    authAuthoritySpringDao.create(authorityEntities);
    return UserJson.fromEntity(
      udUserSpringDao.create(UserEntity.fromJson(user))
    );
  }

  public UserJson createUserJdbcWithOutTransactions(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);

    AuthUserEntity createdAuthUser = authUserDao.create(authUser);

    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
      e -> {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setUserId(createdAuthUser.getId());
        ae.setAuthority(e);
        return ae;
      }
    ).toArray(AuthorityEntity[]::new);

    authAuthorityDao.create(authorityEntities);
    return UserJson.fromEntity(
      udUserDao.create(UserEntity.fromJson(user))
    );
  }

  public UserJson createUserJdbcWithXaTransactions(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDao.create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
          e -> {
            AuthorityEntity ae = new AuthorityEntity();
            ae.setUserId(createdAuthUser.getId());
            ae.setAuthority(e);
            return ae;
          }
        ).toArray(AuthorityEntity[]::new);

        authAuthorityDao.create(authorityEntities);
        return UserJson.fromEntity(
          udUserDao.create(UserEntity.fromJson(user))
        );
      }
    );
  }

  public UserJson createUserChainedTxManager(UserJson user) {
    return getTxTemplate.execute((status) -> {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserSpringDao.create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
          e -> {
            AuthorityEntity ae = new AuthorityEntity();
            ae.setUserId(createdAuthUser.getId());
            ae.setAuthority(e);
            return ae;
          }
        ).toArray(AuthorityEntity[]::new);

        authAuthoritySpringDao.create(authorityEntities);

        UserEntity createdUser = udUserSpringDao.create(UserEntity.fromJson(user));

        return UserJson.fromEntity(createdUser);
      }
    );
  }

  public boolean isUserCreatedInUd(UUID id) {
    Optional<UserEntity> userEntity = udUserSpringDao.findById(id);
    return userEntity.isPresent();
  }

  public boolean isUserCreatedInUserAuth(String username) {
    List<AuthUserEntity> allList = authUserSpringDao.findAll();
    Optional<AuthUserEntity> authUserEntity =
      allList.stream()
        .filter(aue -> aue.getUsername().equals(username))
        .findFirst();
    return authUserEntity.isPresent();
  }

  public boolean isAllAuthoritiesCreated(String username) {
    List<AuthUserEntity> aueList = authUserSpringDao.findAll();
    AtomicInteger count = new AtomicInteger();
    aueList.stream()
      .filter(aue -> aue.getUsername().equals(username))
      .findFirst()
      .ifPresent(
        (authUserEntity) -> {
          List<AuthorityEntity> allList = authAuthoritySpringDao.findAll();
          count.set((int) allList.stream()
            .filter(authority -> authority.getUserId()
              .equals(authUserEntity.getId()))
            .count());
        }
      );
    return count.get() == Authority.values().length;
  }
}
