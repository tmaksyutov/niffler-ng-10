package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.FriendshipDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryJdbc implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    private static final UserDao userDao = new UserDaoJdbc();
    private static final FriendshipDao friendshipDao = new FriendshipDaoJdbc();

    @Nonnull
    @Override
    public UserEntity create(@Nonnull UserEntity user) {
        UserEntity result = userDao.create(user);
        for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
            friendshipDao.createFriendship(friendship);
        }
        for (FriendshipEntity friendship : user.getFriendshipRequests()) {
            friendshipDao.createFriendship(friendship);
        }
        return result;
    }

    @Nonnull
    @Override
    public UserEntity update(@Nonnull UserEntity user) {
        return userDao.update(user);
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findById(@Nonnull UUID id) {
        return userDao.findById(id).map(userEntity -> {
            userEntity.setFriendshipAddressees(friendshipDao.findByAddresseeId(id));
            userEntity.setFriendshipRequests(friendshipDao.findByRequesterId(id));
            return userEntity;
        });
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findByUsername(@Nonnull String username) {
        return userDao.findByUsername(username).map(userEntity -> {
            userEntity.setFriendshipAddressees(friendshipDao.findByAddresseeId(userEntity.getId()));
            userEntity.setFriendshipRequests(friendshipDao.findByRequesterId(userEntity.getId()));
            return userEntity;
        });
    }

    @Override
    public void addFriendshipRequest(
            @Nonnull UserEntity requester,
            @Nonnull UserEntity addressee
    ) {
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setAddressee(addressee);
        friendship.setRequester(requester);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipDao.createFriendship(friendship);
    }

    @Override
    public void addFriend(
            @Nonnull UserEntity requester,
            @Nonnull UserEntity addressee
    ) {
        FriendshipEntity friendshipIncome = new FriendshipEntity();
        friendshipIncome.setAddressee(addressee);
        friendshipIncome.setRequester(requester);
        friendshipIncome.setStatus(FriendshipStatus.ACCEPTED);

        FriendshipEntity friendshipOutcome = new FriendshipEntity();
        friendshipOutcome.setAddressee(requester);
        friendshipOutcome.setRequester(addressee);
        friendshipOutcome.setStatus(FriendshipStatus.ACCEPTED);

        friendshipDao.createFriendship(friendshipIncome);
        friendshipDao.createFriendship(friendshipOutcome);
    }

    @Override
    public void delete(@Nonnull UserEntity user) {
        if (user.getFriendshipAddressees() != null) {
            for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
                if (friendship != null) {
                    friendshipDao.deleteFriendship(friendship);
                }
            }
        }
        if (user.getFriendshipRequests() != null) {
            for (FriendshipEntity friendship : user.getFriendshipRequests()) {
                if (friendship != null) {
                    friendshipDao.deleteFriendship(friendship);
                }
            }
        }
        userDao.delete(user);
    }
}