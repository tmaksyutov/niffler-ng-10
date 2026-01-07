package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {

    UserJson createUser(String username, String password);

    void createIncomeInvitation(UserJson targetUser, int count);

    void createOutcomeInvitation(UserJson targetUser, int count);

    void createFriends(UserJson targetUser, int count);
}