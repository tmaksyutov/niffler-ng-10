package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.RandomDataUtils.randomPassword;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@ExtendWith(BrowserExtension.class)
public class LoginTest {

    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    @DisplayName("Успешная регистрация нового пользователя")
    @Test
    void shouldRegisterNewUser() {
        loginPage
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .fillAndSubmitSuccessRegistration(randomUsername(), randomPassword())
                .checkLoginPageIsLoaded();
    }

    @DisplayName("Ошибка регистрации существующего пользователя")
    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        String username = randomUsername();
        String password = randomPassword();
        String errorMessage = "Username `" + username + "` already exists";

        loginPage
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .fillAndSubmitSuccessRegistration(username, password)
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .setUsername(username)
                .setPassword(password)
                .setConfirmPassword(password)
                .clickRegisterButton()
                .checkErrorMessageIsVisible(errorMessage);
    }

    @DisplayName("Ошибка при несовпадении пароля и подтверждения")
    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = randomUsername();
        String password = randomPassword();
        String wrongConfirmPassword = randomPassword();
        String errorMessage = "Passwords should be equal";

        loginPage
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .setUsername(username)
                .setPassword(password)
                .setConfirmPassword(wrongConfirmPassword)
                .clickRegisterButton()
                .checkErrorMessageIsVisible(errorMessage);
    }

    @DisplayName("Успешный логин существующего пользователя")
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        String username = randomUsername();
        String password = randomPassword();
        loginPage
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .fillAndSubmitSuccessRegistration(username, password)
                .login(username, password)
                .checkMainPageIsLoaded();
    }

    @DisplayName("Ошибка при логине с некорректными данными")
    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String username = randomUsername();
        String correctPassword = randomPassword();
        String wrongPassword = randomPassword();
        loginPage
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .fillAndSubmitSuccessRegistration(username, correctPassword)
                .loginWithBadCredentials(username, wrongPassword)
                .checkLoginPageIsLoaded()
                .checkErrorMessage("Неверные учетные данные пользователя");
    }
}