package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.mapper.FriendshipEntityRowMapper;
import guru.qa.niffler.data.mapper.UserSetExtractor;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositorySpringJdbc implements UserRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement userPs = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getCurrency().name());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setBytes(5, user.getPhoto());
            userPs.setBytes(6, user.getPhotoSmall());
            userPs.setString(7, user.getFullname());
            return userPs;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        List<FriendshipEntity> all = new ArrayList<>();
        all.addAll(user.getFriendshipRequests());
        all.addAll(user.getFriendshipAddressees());

        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (requester_id, addressee_id, status) " +
                        "VALUES (?, ?, ?) " +
                        "ON CONFLICT (requester_id, addressee_id) DO UPDATE SET status = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        FriendshipEntity fr = all.get(i);
                        ps.setObject(1, fr.getRequester().getId());
                        ps.setObject(2, fr.getAddressee().getId());
                        ps.setString(3, fr.getStatus().name());
                        ps.setString(4, fr.getStatus().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return all.size();
                    }
                }
        );

        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT * FROM \"user\" u " +
                                "LEFT JOIN friendship fr " +
                                "ON u.id = fr.requester_id " +
                                "LEFT JOIN friendship fa " +
                                "ON u.id = fa.addressee_id " +
                                "WHERE u.id = ?",
                        UserSetExtractor.instance,
                        id));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT * FROM \"user\" u " +
                                "LEFT JOIN friendship fr " +
                                "ON u.id = fr.requester_id " +
                                "LEFT JOIN friendship fa " +
                                "ON u.id = fa.addressee_id " +
                                "WHERE u.username = ?",
                        UserSetExtractor.instance,
                        username));
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

    public List<FriendshipEntity> getFriendshipRequests(UserEntity user) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return jdbcTemplate.query(
                "SELECT * FROM friendship WHERE requester_id = ?",
                FriendshipEntityRowMapper.instance
        );
    }

    public List<FriendshipEntity> getFriendshipAddressee(UserEntity user) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return jdbcTemplate.query(
                "SELECT * FROM friendship WHERE addressee = ?",
                FriendshipEntityRowMapper.instance
        );
    }

    private void createFriendshipRow(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement friendshipPs = con.prepareStatement(
                    "INSERT INTO friendship (requester_id, addressee_id, status) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (requester_id, addressee_id) " +
                            "DO UPDATE SET status = ? "
            );
            friendshipPs.setObject(1, requester.getId());
            friendshipPs.setObject(2, addressee.getId());
            friendshipPs.setString(3, status.name());
            friendshipPs.setString(4, status.name());
            return friendshipPs;
        });
    }
}