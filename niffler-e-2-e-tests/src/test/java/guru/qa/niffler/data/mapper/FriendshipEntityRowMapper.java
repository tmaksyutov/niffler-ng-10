package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class FriendshipEntityRowMapper implements RowMapper<FriendshipEntity> {
    public static FriendshipEntityRowMapper instance = new FriendshipEntityRowMapper();

    @Override
    public FriendshipEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        FriendshipEntity friendship = new FriendshipEntity();
        UserEntity requester = new UserEntity();
        UserEntity addressee = new UserEntity();
        UUID requesterId = rs.getObject("requester_id", UUID.class);
        UUID addresseeId = rs.getObject("addressee_id", UUID.class);
        FriendshipStatus status = FriendshipStatus.valueOf(rs.getString("status"));
        if (requesterId != null) {
            requester.setId(requesterId);
            friendship.setRequester(requester);
        }
        if (addresseeId != null) {
            addressee.setId(addresseeId);
            friendship.setAddressee(addressee);
        }
        if (status != null) friendship.setStatus(status);
        return friendship;
    }
}