package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOJdbc;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.Arrays;

import static guru.qa.niffler.data.DataBases.xaTransaction;

public class UserDbClient {

  private final Config CFG = Config.getInstance();

  public UserJson createUser(UserJson user, String password) {
    return xaTransaction(
      new XaFunction<>(
        connection -> {
          AuthUserEntity authUserEntity = new AuthUserEntity();
          authUserEntity.setUsername(user.username());
          authUserEntity.setPassword(password);
          AuthUserEntity createdAuthUser = new AuthUserDaoJdbc(connection).create(authUserEntity);

          Arrays.stream(Authority.values())
            .forEach(authority -> {
              AuthorityEntity authorityEntity = new AuthorityEntity();
              authorityEntity.setUserId(createdAuthUser.getId());
              authorityEntity.setAuthority(authority);
              new AuthAuthorityDaoJdbc(connection).create(authorityEntity);
            });
          return null;
        },
        CFG.authJdbcUrl()
      ),
      new XaFunction<>(
        connection -> {
          UserEntity createdUserEntity = new UserdataUserDAOJdbc(connection).createUser(UserEntity.fromJson(user));
          return UserJson.fromEntity(createdUserEntity);
        },
        CFG.userdataJdbcUrl()
      )
    );
  }
}
