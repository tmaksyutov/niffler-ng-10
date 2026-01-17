package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.user.FriendshipEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface FriendshipDao {
    void createFriendship(FriendshipEntity friendship);

    void deleteFriendship(FriendshipEntity friendship);

    @Nonnull
    List<FriendshipEntity> findByRequesterId(UUID requesterId);

    @Nonnull
    List<FriendshipEntity> findByAddresseeId(UUID addresseeId);
}