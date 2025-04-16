package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userData.UserEntity;
import guru.qa.niffler.model.enums.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDataUserEntityRowMapper implements RowMapper<UserEntity> {

  public static final UserDataUserEntityRowMapper instance = new UserDataUserEntityRowMapper();

  private UserDataUserEntityRowMapper() {
  }

  @Override
  public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
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
