package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith({BrowserExtension.class, UserExtension.class})
public class FriendsTest {

    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    @User(
            friends = 1
    )
    @Test
    @DisplayName("Таблица друзей содержит друга")
    void friendShouldBePresentInFriendsTable(UserJson user) {
        loginPage
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkFriend(user.testData().friends().getFirst().username());
    }

    @User
    @Test
    @DisplayName("Таблица друзей пустая")
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        loginPage
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkFriendsEmpty();
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    @DisplayName("Таблица друзей содержит входящий запрос")
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        loginPage
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkRequest(user.testData().incomeInvitations().getFirst().username());
    }

    @User(
            outcomeInvitations = 1
    )
    @Test
    @DisplayName("Список всех людей содержит исходящий запрос")
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        loginPage
                .login(user.username(), user.testData().password())
                .goToAllPeoplePage()
                .checkUserWaiting(user.testData().outcomeInvitations().getFirst().username());
    }
}