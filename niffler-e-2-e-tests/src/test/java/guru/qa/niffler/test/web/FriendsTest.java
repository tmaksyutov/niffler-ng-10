package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.StaticUser;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;


@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsTest {

    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    @Test
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        loginPage
                .login(user.username(), user.password())
                .goToFriendsPage()
                .checkFriend(user.friend());
    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(WITHOUT_FRIEND) StaticUser user) {
        loginPage
                .login(user.username(), user.password())
                .goToFriendsPage()
                .checkFriendsEmpty();
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        loginPage
                .login(user.username(), user.password())
                .goToFriendsPage()
                .checkRequest(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        loginPage
                .login(user.username(), user.password())
                .goToAllPeoplePage()
                .checkUserWaiting(user.outcome());
    }
}