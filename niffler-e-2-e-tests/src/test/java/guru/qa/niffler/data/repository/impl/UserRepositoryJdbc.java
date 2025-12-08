package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserRepositoryJdbc implements UserRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (currency, firstname, full_name, photo, photo_small, surname, username) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            userPs.setString(1, user.getCurrency().name());
            userPs.setString(2, user.getFirstname());
            userPs.setString(3, user.getFullname());
            userPs.setBytes(4, user.getPhoto());
            userPs.setBytes(5, user.getPhotoSmall());
            userPs.setString(6, user.getSurname());
            userPs.setString(7, user.getUsername());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u " +
                        "JOIN friendship fr " +
                        "ON u.id = fr.requester_id " +
                        "JOIN friendship fa " +
                        "ON u.id = fa.addressee_id " +
                        "WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(getUser(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u " +
                        "JOIN friendship fr " +
                        "ON u.id = fr.requester_id " +
                        "JOIN friendship fa " +
                        "ON u.id = fa.addressee_id " +
                        "WHERE u.username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(getUser(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void addInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipRow(requester, addressee, FriendshipStatus.PENDING);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        createFriendshipRow(requester, addressee, FriendshipStatus.ACCEPTED);
        createFriendshipRow(addressee, requester, FriendshipStatus.ACCEPTED);
    }

    private void createFriendshipRow(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        try (PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status) " +
                        "VALUES (?, ?, ?) " +
                        "ON CONFLICT (requester_id, addressee_id) " +
                        "DO UPDATE SET status = ? "
        )) {
            friendshipPs.setObject(1, requester.getId());
            friendshipPs.setObject(2, addressee.getId());
            friendshipPs.setString(3, status.name());
            friendshipPs.setString(4, status.name());
            friendshipPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        List<UserEntity> users = new ArrayList<>();

        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(

                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    users.add(getUser(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?");
             PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM friendship WHERE requester_id = ? " +
                             "OR addressee_id  = ?");
        ) {
            friendshipPs.setObject(1, user.getId());
            friendshipPs.setObject(2, user.getId());
            friendshipPs.executeUpdate();
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<FriendshipEntity> getFriendshipRequests(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM friendship WHERE requester_id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return getFriendships(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FriendshipEntity> getFriendshipAddressee(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM friendship WHERE addressee_id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return getFriendships(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private UserEntity getUser(ResultSet rs) throws SQLException {
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


    private FriendshipEntity getFriendship(ResultSet rs) throws SQLException {
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

    private List<FriendshipEntity> getFriendships(ResultSet rs) throws SQLException {
        List<FriendshipEntity> friendshipEntities = new ArrayList<>();
        while (rs.next()) friendshipEntities.add(getFriendship(rs));
        return friendshipEntities;
    }
}