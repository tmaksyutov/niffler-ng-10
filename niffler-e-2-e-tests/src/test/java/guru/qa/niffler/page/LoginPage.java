package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement
            usernameInput = $("#username"),
            passwordInput = $("#password"),
            submitBtn = $("#login-button"),
            registerBtn = $("#register-button"),
            errorMessage = $(".form__error");

    @Step("Login with username: '{username}' and password: '{password}'")
    public MainPage login(String username, String password) {
        usernameInput.val(username);
        passwordInput.val(password);
        submitBtn.click();
        return new MainPage();
    }

    @Step("Login with username: '{username}' and incorrect password: '{password}'")
    public LoginPage loginWithBadCredentials(String username, String incorrectPassword) {
        usernameInput.val(username);
        passwordInput.val(incorrectPassword);
        submitBtn.click();
        return this;
    }

    @Step("Check error message: '{message}'")
    public LoginPage checkErrorMessage(String message) {
        errorMessage.shouldHave(text(message));
        return this;
    }

    @Step("Go to registration page")
    public RegistrationPage goToRegistrationPage() {
        registerBtn.click();
        return new RegistrationPage();
    }

    @Step("Check that login page is loaded")
    public LoginPage checkLoginPageIsLoaded() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        submitBtn.shouldBe(visible);
        registerBtn.shouldBe(visible);
        return this;
    }

}