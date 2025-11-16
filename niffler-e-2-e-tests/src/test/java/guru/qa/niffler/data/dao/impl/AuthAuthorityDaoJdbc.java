package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(AuthorityEntity... authorities) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity authority : authorities) {
                statement.setObject(1, authority.getUser().getId());
                statement.setString(2, authority.getAuthority().name());
                statement.addBatch();
                statement.clearParameters();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> findByUserId(UUID id) {
        List<AuthorityEntity> authorities = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority WHERE user_id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    authorities.add(getAuthority(rs));
                }
                return authorities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(getAuthority(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthorityEntity authority) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM authority WHERE id = ?"
        )) {
            ps.setObject(1, authority.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private AuthorityEntity getAuthority(ResultSet rs) throws SQLException {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setId(rs.getObject("id", UUID.class));
        authority.setAuthority(rs.getObject("authority", Authority.class));
        AuthUserEntity userAuth = new AuthUserEntity();
        userAuth.setId(rs.getObject("user_id", UUID.class));
        authority.setUser(userAuth);
        return authority;
    }
}