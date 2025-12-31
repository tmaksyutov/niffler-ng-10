package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;

import guru.qa.niffler.data.mapper.UserSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
                        "LEFT JOIN friendship fr " +
                        "ON u.id = fr.requester_id " +
                        "LEFT JOIN friendship fa " +
                        "ON u.id = fa.addressee_id " +
                        "WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                UserEntity user = UserSetExtractor.instance.extractData(rs);
                return Optional.ofNullable(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u " +
                        "LEFT JOIN friendship fr " +
                        "ON u.id = fr.requester_id " +
                        "LEFT JOIN friendship fa " +
                        "ON u.id = fa.addressee_id " +
                        "WHERE u.username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                UserEntity user = UserSetExtractor.instance.extractData(rs);
                return Optional.ofNullable(user);
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