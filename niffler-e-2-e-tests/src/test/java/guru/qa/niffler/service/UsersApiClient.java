package guru.qa.niffler.service;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.UsersApi;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.eclipse.jetty.http.HttpStatus;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
public class UsersApiClient extends RestClient implements UsersClient {

    private final UsersApi usersApi;
    private final AuthApiClient authApiClient = new AuthApiClient();

    public UsersApiClient() {
        super(CFG.userdataUrl());
        this.usersApi = create(UsersApi.class);
    }

    @Nonnull
    @Step("Create user with username '{username}'")
    @Override
    public UserJson createUser(String username, String password) {
        Response<UserJson> response;
        try {
            Response<Void> authResponse = authApiClient.register(username, password);
            assertThat(authResponse.code()).isEqualTo(HttpStatus.OK_200);

            Stopwatch sw = Stopwatch.createStarted();
            long maxWaitTime = 10_000;

            while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
                try {
                    UserJson userJson = usersApi.currentUser(username).execute().body();
                    if (userJson != null && userJson.id() != null) {
                        return userJson;
                    } else {
                        Thread.sleep(100);
                    }
                } catch (IOException e) {
                    // just wait
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        throw new AssertionError("User was not created");
    }

    @Nonnull
    @Step("Add {count} income invitations for user '{targetUser.username}'")
    @Override
    public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserJson user = createUser(RandomDataUtils.randomUsername(), UserExtension.DEFAULT_PASSWORD);
            try {
                Response<UserJson> response = usersApi.sendInvitation(user.username(), targetUser.username()).execute();
                assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
            } catch (IOException e) {
                throw new AssertionError("Failed to send invitation: " + e.getMessage(), e);
            }
        }

        Response<List<UserJson>> response;
        try {
            response = usersApi.friends(targetUser.username()).execute();
            assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
        } catch (IOException e) {
            throw new AssertionError("Failed to get friends list: " + e.getMessage(), e);
        }

        List<UserJson> friends = response.body();
        if (friends == null) {
            return List.of();
        }

        return friends.stream()
                .filter(user -> user.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED)
                .collect(Collectors.toList());
    }

    @Nonnull
    @Step("Add {count} outcome invitations from user '{targetUser.username}'")
    @Override
    public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
        List<UserJson> invitations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserJson user = createUser(RandomDataUtils.randomUsername(), UserExtension.DEFAULT_PASSWORD);
            try {
                Response<UserJson> response = usersApi.sendInvitation(targetUser.username(), user.username()).execute();
                assertThat(response.code()).isEqualTo(HttpStatus.OK_200);

                UserJson invitation = response.body();
                if (invitation != null) {
                    invitations.add(invitation);
                }
            } catch (IOException e) {
                throw new AssertionError("Failed to send invitation: " + e.getMessage(), e);
            }
        }
        return invitations;
    }

    @Nonnull
    @Step("Add {count} friends for user '{targetUser.username}'")
    @Override
    public List<UserJson> addFriend(UserJson targetUser, int count) {
        List<UserJson> friends = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserJson user = createUser(RandomDataUtils.randomUsername(), UserExtension.DEFAULT_PASSWORD);
            try {
                Response<UserJson> sendResponse = usersApi.sendInvitation(user.username(), targetUser.username()).execute();
                assertThat(sendResponse.code()).isEqualTo(HttpStatus.OK_200);

                Response<UserJson> acceptResponse = usersApi.acceptInvitation(targetUser.username(), user.username()).execute();
                assertThat(acceptResponse.code()).isEqualTo(HttpStatus.OK_200);

                UserJson friend = acceptResponse.body();
                if (friend != null) {
                    friends.add(friend);
                }
            } catch (IOException e) {
                throw new AssertionError("Failed to add friend: " + e.getMessage(), e);
            }
        }
        return friends;
    }

    @Nonnull
    public List<UserJson> getAll(String username) {
        List<UserJson> resultList = null;
        try {
            Response<List<UserJson>> response = usersApi.allUsers(username, null).execute();
            assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
            resultList = response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultList != null ? resultList : List.of();
    }
}