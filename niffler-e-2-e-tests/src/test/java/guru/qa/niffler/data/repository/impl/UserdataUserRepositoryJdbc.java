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
import guru.qa.niffler.data.tpl.XaTransactionTemplate;

import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    private static final UserDao userDao = new UserDaoJdbc();
    private static final FriendshipDao friendshipDao = new FriendshipDaoJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.userdataJdbcUrl()
    );

    @Override
    public UserEntity create(UserEntity user) {
        return xaTransactionTemplate.execute(() -> {
            UserEntity result = userDao.create(user);
            for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
                friendshipDao.createFriendship(friendship);
            }
            for (FriendshipEntity friendship : user.getFriendshipRequests()) {
                friendshipDao.createFriendship(friendship);
            }
            return result;
        });
    }

    @Override
    public UserEntity update(UserEntity user) {
        return userDao.update(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return userDao.findById(id).map(userEntity -> {
            userEntity.setFriendshipAddressees(friendshipDao.findByAddresseeId(id));
            userEntity.setFriendshipRequests(friendshipDao.findByRequesterId(id));
            return userEntity;
        });
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userDao.findByUsername(username).map(userEntity -> {
            userEntity.setFriendshipAddressees(friendshipDao.findByAddresseeId(userEntity.getId()));
            userEntity.setFriendshipRequests(friendshipDao.findByRequesterId(userEntity.getId()));
            return userEntity;
        });
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setAddressee(addressee);
        friendship.setRequester(requester);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipDao.createFriendship(friendship);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        xaTransactionTemplate.execute(() -> {
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
            return null;
        });
    }

    @Override
    public void delete(UserEntity user) {
        xaTransactionTemplate.execute(() -> {
                    for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
                        friendshipDao.deleteFriendship(friendship);
                    }
                    for (FriendshipEntity friendship : user.getFriendshipRequests()) {
                        friendshipDao.deleteFriendship(friendship);
                    }
                    userDao.delete(user);
                    return null;
                }
        );
    }
}