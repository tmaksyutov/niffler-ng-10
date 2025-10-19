package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.DataUtils.getRandomPassword;
import static guru.qa.niffler.utils.DataUtils.getRandomUserName;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        loginPage
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .fillAndSubmitSuccessRegistration(getRandomUserName(), getRandomPassword())
                .checkLoginPageIsLoaded();
    }

    @Test
    @DisplayName("Ошибка регистрации существующего пользователя")
    void shouldNotRegisterUserWithExistingUsername() {
        String username = getRandomUserName();
        String password = getRandomPassword();
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

    @Test
    @DisplayName("Ошибка при несовпадении пароля и подтверждения")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = getRandomUserName();
        String password = getRandomPassword();
        String wrongConfirmPassword = getRandomPassword();
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


    @Test
    @DisplayName("Успешный логин существующего пользователя")
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        String username = getRandomUserName();
        String password = getRandomPassword();
        loginPage
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .fillAndSubmitSuccessRegistration(username, password)
                .login(username, password)
                .checkMainPageIsLoaded();
    }

    @Test
    @DisplayName("Ошибка при логине с некорректными данными")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String username = getRandomUserName();
        String correctPassword = getRandomPassword();
        String wrongPassword = getRandomPassword();
        loginPage
                .goToRegistrationPage()
                .checkRegistrationPageIsLoaded()
                .fillAndSubmitSuccessRegistration(username, correctPassword)
                .loginWithBadCredentials(username, wrongPassword)
                .checkLoginPageIsLoaded()
                .checkErrorMessage("Неверные учетные данные пользователя");
    }
}