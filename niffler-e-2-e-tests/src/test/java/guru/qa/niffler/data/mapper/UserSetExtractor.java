package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserSetExtractor implements ResultSetExtractor<UserEntity> {

    public static UserSetExtractor instance = new UserSetExtractor();

    private UserSetExtractor() {
    }

    @Override
    public UserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, UserEntity> users = new ConcurrentHashMap<>();
        UserEntity user = null;

        while (rs.next()) {
            UUID userId = rs.getObject("id", UUID.class);

            user = users.computeIfAbsent(userId, id -> {
                UserEntity result = new UserEntity();
                try {
                    result.setId(rs.getObject("id", UUID.class));
                    result.setUsername(rs.getString("username"));
                    result.setCurrency(CurrencyValues.valueOf(rs.getString("u.currency")));
                    result.setFirstname(rs.getString("firstname"));
                    result.setSurname(rs.getString("surname"));
                    result.setFullname(rs.getString("full_name"));
                    result.setPhoto(rs.getBytes("photo"));
                    result.setPhotoSmall(rs.getBytes("photo_small"));
                    return result;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            List<FriendshipEntity> friendshipRequests = user.getFriendshipRequests();
            List<FriendshipEntity> friendshipAddressee = user.getFriendshipAddressees();
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

            if (requesterId.equals(userId)) friendshipRequests.add(friendship);
            if (addresseeId.equals(userId)) friendshipAddressee.add(friendship);

            user.setFriendshipRequests(friendshipRequests);
            user.setFriendshipAddressees(friendshipAddressee);
        }
        return user;
    }
}