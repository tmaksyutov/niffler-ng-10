package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.mapper.UserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDaoSpringJdbc implements UserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public UserEntity update(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        try {
            int count = jdbcTemplate.update(con -> {
                PreparedStatement usersPs = con.prepareStatement(
                        "UPDATE \"user\" " +
                                "SET currency = ?, " +
                                "firstname   = ?, " +
                                "surname     = ?, " +
                                "photo       = ?, " +
                                "photo_small = ? " +
                                "WHERE id = ?"
                );
                usersPs.setString(1, user.getCurrency().name());
                usersPs.setString(2, user.getFirstname());
                usersPs.setString(3, user.getSurname());
                usersPs.setBytes(4, user.getPhoto());
                usersPs.setBytes(5, user.getPhotoSmall());
                usersPs.setObject(6, user.getId());
                return usersPs;
            });
            if (count == 0) throw new SQLException("Can`t find category by id");
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        throw new RuntimeException("User with username '" + username + "' not found");
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                UserEntityRowMapper.instance
        );
    }

    @Override
    public void delete(UserEntity user) {
        throw new UnsupportedOperationException();
    }
}