package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.model.enums.CurrencyValues;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class UserdataUserDAOJdbc implements UserdataUserDAO {

  private static final Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public UserEntity create(UserEntity user) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getSurname());
      ps.setBytes(5, user.getPhoto());
      ps.setBytes(6, user.getPhotoSmall());
      ps.setString(7, user.getFullname());
      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
        user.setId(generatedKey);
        return user;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "SELECT * FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          UserEntity ue = fillUserEntity(rs);
          return Optional.of(ue);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "SELECT * FROM \"user\" WHERE username = ?"
    )) {
      ps.setString(1, username);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          UserEntity ue = fillUserEntity(rs);
          return Optional.of(ue);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public List<UserEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "Select * FROM \"user\""
    )) {
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        List<UserEntity> resultEntityList = new ArrayList<>();
        while (rs.next()) {
          UserEntity ue = fillUserEntity(rs);
          resultEntityList.add(ue);
        }
        return resultEntityList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(UserEntity user) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
      "DELETE FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static UserEntity fillUserEntity(ResultSet rs) throws SQLException {
    UserEntity ue = new UserEntity();
    ue.setId(rs.getObject("id", UUID.class));
    ue.setUsername(rs.getString("username"));
    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    ue.setFirstname(rs.getString("firstname"));
    ue.setSurname(rs.getString("surname"));
    ue.setPhoto(rs.getBytes("photo"));
    ue.setPhotoSmall(rs.getBytes("photo_small"));
    ue.setFullname(rs.getString("full_name"));
    return ue;
  }
}
