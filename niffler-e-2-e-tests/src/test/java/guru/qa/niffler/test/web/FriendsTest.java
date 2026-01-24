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

    @DisplayName("Таблица друзей содержит друга")
    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        loginPage
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .friendsTableShouldContainFriend(user.testData().friends().getFirst().username());
    }

    @DisplayName("Таблица друзей пустая")
    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        loginPage
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .friendsTableShouldBeEmpty();
    }

    @DisplayName("Таблица друзей содержит входящий запрос")
    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        loginPage
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .requestsTableShouldContainIncomeFriend(user.testData().incomeInvitations().getFirst().username());
    }

    @DisplayName("Список всех людей содержит исходящий запрос")
    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        loginPage
                .login(user.username(), user.testData().password())
                .goToAllPeoplePage()
                .allPeoplesTableShouldContainWaitingAnswerFromFriend(user.testData().outcomeInvitations().getFirst().username());
    }

    @DisplayName("Прием заявки в друзья")
    @User(incomeInvitations = 1)
    @Test
    void acceptIncomeInvitation(UserJson user) {
        String friendName = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .requestsTableShouldContainIncomeFriend(friendName)
                .acceptInvitationFrom(friendName)
                .checkSnackbarText("Invitation of %s accepted".formatted(friendName))
                .friendsTableShouldContainFriend(friendName);
    }

    @DisplayName("Отклонение заявки в друзья")
    @User(incomeInvitations = 1)
    @Test
    void declineIncomeInvitation(UserJson user) {
        String friendName = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .requestsTableShouldContainIncomeFriend(friendName)
                .declineInvitationFrom(friendName)
                .checkSnackbarText("Invitation of %s is declined".formatted(friendName))
                .friendsTableShouldBeEmpty();
    }
}