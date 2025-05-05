package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.enums.CurrencyValues;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UsersDbClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
  public static final String DEFAULT_PASSWORD = "12345";

  private final AuthUserRepository authUserRepo = new AuthUserRepositoryHibernate();
  private final UserdataUserRepository userdataUserRepo = new UserdataUserRepositoryHibernate();

//  private final AuthUserRepository authUserRepo = new AuthUserRepositorySpringJdbc();
//  private final UserdataUserRepository userdataUserRepo = new UserdataRepositorySpringJdbc();

//  private final AuthUserRepository authUserRepo = new AuthUserRepositoryJdbc();
//  private final UserdataUserRepository userdataUserRepo = new UserdataUserRepositoryJdbc();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
    CFG.authJdbcUrl(),
    CFG.userdataJdbcUrl()
  );

  @Override
  public UserJson createUser(String username, String password) {
    return xaTransactionTemplate.execute(() -> {
        AuthUserEntity authUser = authUserEntity(username, password);

        authUserRepo.create(authUser);
        return UserJson.fromEntity(
          userdataUserRepo.create(userEntity(username))
        );
      }
    );
  }

  @Override
  //Приглашения от addressee targetUse-У
  public void createIncomeInvitations(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = findUserEntity(targetUser);

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
            String username = RandomDataUtils.randomUserName();
            AuthUserEntity authUser = authUserEntity(username, DEFAULT_PASSWORD);
            authUserRepo.create(authUser);
            UserEntity addressee = userdataUserRepo.create(userEntity(username));
            userdataUserRepo.addInvitation(addressee, targetEntity);
          targetUser.testData().incomeRequests().add(UserJson.fromEntity(addressee));
            return null;
          }
        );
      }
    }
  }

  @Override
  public void createOutcomeInvitations(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = findUserEntity(targetUser);

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
            String username = RandomDataUtils.randomUserName();
            AuthUserEntity authUser = authUserEntity(username, DEFAULT_PASSWORD);
            authUserRepo.create(authUser);
            UserEntity addressee = userdataUserRepo.create(userEntity(username));
            userdataUserRepo.addInvitation(targetEntity, addressee);
          targetUser.testData().outcomeRequests().add(UserJson.fromEntity(addressee));
            return null;
          }
        );
      }
    }
  }

  @Override
  public void createFriends(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = findUserEntity(targetUser);

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
            String username = RandomDataUtils.randomUserName();
            AuthUserEntity authUser = authUserEntity(username, DEFAULT_PASSWORD);
            authUserRepo.create(authUser);
            UserEntity addressee = userdataUserRepo.create(userEntity(username));
            userdataUserRepo.addFriend(targetEntity, addressee);
          targetUser.testData().friends().add(UserJson.fromEntity(addressee));
            return null;
          }
        );
      }
    }
  }

  @Override
  public UserJson findUserByUsername(String userName) {
    UserEntity userEntity = userdataUserRepo.findByUsername(userName)
      .orElseThrow(
        () -> new IllegalArgumentException("User " + userName + " doesnt exist")
      );
    return UserJson.fromEntity(userEntity);
  }

  //Todo так же удалять и spending, и categories
  @Override
  public void remove(UserJson user) {
    xaTransactionTemplate.execute(() -> {
        Optional<AuthUserEntity> authUserOpt = authUserRepo.findByUsername(user.username());
        authUserOpt.ifPresent(authUserRepo::remove);
        userdataUserRepo.remove(UserEntity.fromJson(user));
        return null;
      }
    );
  }

  private UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    ue.setCurrency(CurrencyValues.RUB);
    return ue;
  }

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

  private UserEntity findUserEntity(UserJson targetUser) {
    UUID id = targetUser.id();
    String username = targetUser.username();

    if (id == null && username == null) {
      throw new IllegalArgumentException("User ID and username cannot both be null");
    }

    Optional<UserEntity> entityOpt = (id != null)
      ? userdataUserRepo.findById(id)
      : Optional.empty();

    UserEntity targetEntity = entityOpt.or(() ->
      username != null
        ? userdataUserRepo.findByUsername(username)
        : Optional.empty()
    ).orElseThrow(() -> new EntityNotFoundException("Target user not found by id or username"));
    return targetEntity;
  }
}
