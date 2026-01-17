package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.mapper.FriendshipEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

public class FriendshipDaoSpringJdbc implements FriendshipDao {
    private static final Config CFG = Config.getInstance();

    @Override
    public void createFriendship(@Nonnull FriendshipEntity friendship) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO friendship (requester_id, addressee_id, status) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (requester_id, addressee_id) DO UPDATE SET status = ?");
            ps.setObject(1, friendship.getRequester().getId());
            ps.setObject(2, friendship.getAddressee().getId());
            ps.setString(3, friendship.getStatus().name());
            ps.setString(4, friendship.getStatus().name());
            return ps;
        });
    }

    @Override
    public void deleteFriendship(@Nonnull FriendshipEntity friendship) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "DELETE FROM friendship WHERE requester_id = ? AND addressee_id = ?",
                friendship.getRequester().getId(),
                friendship.getAddressee().getId()
        );
    }

    @Nonnull
    @Override
    public List<FriendshipEntity> findByRequesterId(@Nonnull UUID requesterId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM friendship WHERE requester_id = ?",
                FriendshipEntityRowMapper.instance,
                requesterId
        );
    }

    @Nonnull
    @Override
    public List<FriendshipEntity> findByAddresseeId(@Nonnull UUID addresseeId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM friendship WHERE addressee_id = ?",
                FriendshipEntityRowMapper.instance,
                addresseeId
        );
    }
}