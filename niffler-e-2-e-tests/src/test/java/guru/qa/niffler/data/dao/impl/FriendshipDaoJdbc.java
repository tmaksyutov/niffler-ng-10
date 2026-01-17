package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class FriendshipDaoJdbc implements FriendshipDao {
    private static final Config CFG = Config.getInstance();

    @Override
    public void createFriendship(@Nonnull FriendshipEntity friendship) {
        try (PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status) " +
                        "VALUES (?, ?, ?) " +
                        "ON CONFLICT (requester_id, addressee_id) " +
                        "DO UPDATE SET status = ? "
        )) {
            friendshipPs.setObject(1, friendship.getRequester().getId());
            friendshipPs.setObject(2, friendship.getAddressee().getId());
            friendshipPs.setString(3, friendship.getStatus().name());
            friendshipPs.setString(4, friendship.getStatus().name());
            friendshipPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFriendship(@Nonnull FriendshipEntity friendship) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM friendship WHERE requester_id = ? AND addressee_id = ?"
        )) {
            ps.setObject(1, friendship.getRequester());
            ps.setObject(2, friendship.getAddressee());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Nonnull
    @Override
    public List<FriendshipEntity> findByRequesterId(@Nonnull UUID requesterId) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM friendship WHERE requester_id = ?"
        )) {
            ps.setObject(1, requesterId);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return getFriendships(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public List<FriendshipEntity> findByAddresseeId(@Nonnull UUID addresseeId) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM friendship WHERE addressee_id = ?"
        )) {
            ps.setObject(1, addresseeId);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return getFriendships(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    private FriendshipEntity getFriendship(@Nonnull ResultSet rs) throws SQLException {
        FriendshipEntity friendship = new FriendshipEntity();

        UserEntity requester = new UserEntity();
        UserEntity addressee = new UserEntity();

        requester.setId(rs.getObject("requester_id", UUID.class));

        addressee.setId(rs.getObject("addressee_id", UUID.class));

        friendship.setAddressee(addressee);
        friendship.setRequester(requester);
        friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
        return friendship;
    }

    @Nonnull
    private List<FriendshipEntity> getFriendships(@Nonnull ResultSet rs) throws SQLException {
        List<FriendshipEntity> friendshipEntities = new ArrayList<>();
        while (rs.next()) friendshipEntities.add(getFriendship(rs));
        return friendshipEntities;
    }
}