package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface AuthAuthorityDao {

  void create(AuthorityEntity entity);

  void create(AuthorityEntity... authority);

  @Nonnull
  List<AuthorityEntity> findAll();

  void delete(AuthorityEntity entity);
}
