package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.user.FriendshipEntity;

import java.util.List;
import java.util.UUID;

public interface FriendshipDao {
    void createFriendship(FriendshipEntity friendship);

    void deleteFriendship(FriendshipEntity friendship);

    List<FriendshipEntity> findByRequesterId(UUID requesterId);

    List<FriendshipEntity> findByAddresseeId(UUID addresseeId);
}