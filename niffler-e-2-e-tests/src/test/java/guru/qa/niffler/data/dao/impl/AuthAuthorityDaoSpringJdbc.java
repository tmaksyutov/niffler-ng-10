package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {
    private final DataSource dataSource;

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(AuthorityEntity... authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUser().getId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public List<AuthorityEntity> findByUserId(UUID id) {
        return List.of();
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<AuthorityEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.query(
                "SELECT * FROM authority",
                new RowMapper<AuthorityEntity>() {
                    @Override
                    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                        AuthorityEntity authority = new AuthorityEntity();
                        authority.setId(rs.getObject("id", UUID.class));
                        authority.setAuthority(rs.getObject("authority", Authority.class));
                        AuthUserEntity userAuth = new AuthUserEntity();
                        userAuth.setId(rs.getObject("user_id", UUID.class));
                        authority.setUser(userAuth);
                        return authority;
                    }
                }
        );
    }

    @Override
    public void delete(AuthorityEntity authority) {
        throw new UnsupportedOperationException();
    }
}