package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.dao.impl.spring.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.UserdataUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserdataRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UserDbClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
  private final UserdataUserDAO udUserDao = new UserdataUserDAOSpringJdbc();

  private final AuthUserRepository authUserRepo = new AuthUserRepositoryJdbc();
  private final UserdataUserRepository userdataUserRepo = new UserdataUserRepositoryJdbc();

  private final AuthUserRepository authUserSpringRepo = new AuthUserRepositorySpringJdbc();
  private final UserdataUserRepository userdataUserSpringRepo = new UserdataRepositorySpringJdbc();

  private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();
  private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();

  private final TransactionTemplate txTemplate = new TransactionTemplate(
    new JdbcTransactionManager(
      DataSources.dataSource(CFG.authJdbcUrl())
    )
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
    CFG.authJdbcUrl(),
    CFG.userdataJdbcUrl()
  );

  private AuthUserEntity authUserEntity(String username, String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode(password));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(
      Arrays.stream(Authority.values()).map(
        e -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setUser(authUser);
          ae.setAuthority(e);
          return ae;
        }
      ).toList()
    );
    return authUser;
  }

  public UserJson createUser(UserJson user) {
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
            ae.setUser(createdAuthUser);
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

  public UserJson createUserRepo(String username, String password) {
    return xaTransactionTemplate.execute(() -> {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
          Arrays.stream(Authority.values()).map(
            e -> {
              AuthorityEntity ae = new AuthorityEntity();
              ae.setUser(authUser);
              ae.setAuthority(e);
              return ae;
            }
          ).toList()
        );

        authUserRepo.create(authUser);
        return UserJson.fromEntity(
          userdataUserRepo.create(userEntity(username))
        );
      }
    );
  }

  public void addInvitation(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
        targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
            String username = RandomDataUtils.randomUserName();
            AuthUserEntity authUser = authUserEntity(username, "12345");
            authUserRepository.create(authUser);
            UserEntity adressee = userdataUserRepository.create(userEntity(username));
            userdataUserRepository.addInvitation(targetEntity, adressee);
            return null;
          }
        );
      }
    }
  }

  private UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    ue.setCurrency(CurrencyValues.RUB);
    return ue;
  }

  public UserEntity findUserByIdRepo(UUID id) {
    return xaTransactionTemplate.execute(() -> {
        Optional<UserEntity> findedUe = userdataUserRepo.findById(id);
        if (findedUe.isPresent()) {
          return findedUe.get();
        } else return null;
      }
    );
  }

  public void addInvitationRepo(UserJson requester, UserJson addressee) {
    xaTransactionTemplate.execute(() -> {
        UserEntity reqEntity = UserEntity.fromJson(requester);
        UserEntity addrEntity = UserEntity.fromJson(addressee);
        userdataUserRepo.addInvitation(reqEntity, addrEntity);

        reqEntity.getFriendshipRequests().forEach(System.out::println);
        addrEntity.getFriendshipRequests().forEach(System.out::println);

        return null;
      }
    );
  }

  public void addFriendRepo(UserJson requester, UserJson addressee) {
    xaTransactionTemplate.execute(() -> {
      UserEntity reqEntity = UserEntity.fromJson(requester);
      UserEntity addrEntity = UserEntity.fromJson(addressee);

      if (requester.id() == null || addressee.id() == null) {
        throw new IllegalArgumentException("User id should be not null");
      }

      userdataUserRepo.addFriend(reqEntity, addrEntity);

      reqEntity.getFriendshipRequests().forEach(System.out::println);
      addrEntity.getFriendshipRequests().forEach(System.out::println);

      return null;
    });
  }

  public UserJson createUserSpringRepo(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
          Arrays.stream(Authority.values()).map(
            e -> {
              AuthorityEntity ae = new AuthorityEntity();
              ae.setUser(authUser);
              ae.setAuthority(e);
              return ae;
            }
          ).toList()
        );

        AuthUserEntity createdAuthUserEntity = authUserSpringRepo.create(authUser);
        System.out.println(createdAuthUserEntity.getAuthorities());
        return UserJson.fromEntity(
          userdataUserSpringRepo.create(UserEntity.fromJson(user))
        );
      }
    );
  }

  public AuthUserEntity findAuthUserByIdSpringRepo(UUID id) {
    return xaTransactionTemplate.execute(() -> {
        Optional<AuthUserEntity> authUserEntity = authUserSpringRepo.findById(id);
        return authUserEntity.orElse(null);
      }
    );
  }

  public UserEntity findUserByIdSpringRepo(UUID id) {
    return xaTransactionTemplate.execute(() -> {
        Optional<UserEntity> findedUe = userdataUserSpringRepo.findById(id);
        return findedUe.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
      }
    );
  }

  public void addInvitationSpringRepo(UserJson requester, UserJson addressee) {
    xaTransactionTemplate.execute(() -> {
        UserEntity reqEntity = UserEntity.fromJson(requester);
        UserEntity addrEntity = UserEntity.fromJson(addressee);
        userdataUserSpringRepo.addInvitation(reqEntity, addrEntity);

        reqEntity.getFriendshipRequests().forEach(System.out::println);
        addrEntity.getFriendshipRequests().forEach(System.out::println);

        return null;
      }
    );
  }

  public void addFriendSpringRepo(UserJson requester, UserJson addressee) {
    xaTransactionTemplate.execute(() -> {
      UserEntity reqEntity = UserEntity.fromJson(requester);
      UserEntity addrEntity = UserEntity.fromJson(addressee);

      if (requester.id() == null || addressee.id() == null) {
        throw new IllegalArgumentException("User id should be not null");
      }

      userdataUserSpringRepo.addFriend(reqEntity, addrEntity);

      reqEntity.getFriendshipRequests().forEach(System.out::println);
      addrEntity.getFriendshipRequests().forEach(System.out::println);

      return null;
    });
  }
}
