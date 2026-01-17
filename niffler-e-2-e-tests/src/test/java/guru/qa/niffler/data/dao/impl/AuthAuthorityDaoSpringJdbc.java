package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(@Nonnull AuthorityEntity... authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
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

    @Nonnull
    @Override
    public List<AuthorityEntity> findByUserId(@Nonnull UUID id) {
        throw new RuntimeException("Authorities for user with id " + id + " not found");
    }

    @Nonnull
    @Override
    public Optional<AuthorityEntity> findById(@Nonnull UUID id) {
        throw new RuntimeException("AuthorityEntity with id " + id + " not found");
    }

    @Nonnull
    @Override
    public List<AuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM authority",
                new RowMapper<AuthorityEntity>() {
                    @Nonnull
                    @Override
                    public AuthorityEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
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
    public void delete(@Nonnull AuthorityEntity authority) {
        throw new UnsupportedOperationException();
    }
}