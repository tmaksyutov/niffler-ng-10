package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegistrationPage extends BasePage<RegistrationPage> {
    private final SelenideElement
            usernameInput = $("#username"),
            passwordInput = $("#password"),
            confirmPasswordInput = $("#passwordSubmit"),
            registerBtn = $("#register-button"),
            signInBtn = $(".form_sign-in"),
            errorLabel = $(".form__error");

    @Nonnull
    @Step("Set username: '{username}'")
    public RegistrationPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Nonnull
    @Step("Set password")
    public RegistrationPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Nonnull
    @Step("Set password confirmation")
    public RegistrationPage setConfirmPassword(String password) {
        confirmPasswordInput.setValue(password);
        return this;
    }

    @Nonnull
    @Step("Click registration button")
    public RegistrationPage clickRegisterButton() {
        registerBtn.click();
        return this;
    }

    @Nonnull
    @Step("Submit registration")
    public LoginPage submitSuccessRegistration() {
        registerBtn.click();
        signInBtn.click();
        return new LoginPage();
    }

    @Nonnull
    @Step("Fill and submit successful registration with username: '{username}' and password: '{password}'")
    public LoginPage fillAndSubmitSuccessRegistration(String username, String password) {
        return this.setUsername(username)
                .setPassword(password)
                .setConfirmPassword(password)
                .submitSuccessRegistration();
    }

    @Nonnull
    @Step("Check error message: '{message}'")
    public RegistrationPage checkErrorMessageIsVisible(String message) {
        errorLabel.shouldHave(visible).shouldHave(text(message));
        return this;
    }

    @Nonnull
    @Step("Check that registration page is loaded")
    public RegistrationPage checkRegistrationPageIsLoaded() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        confirmPasswordInput.shouldBe(visible);
        registerBtn.shouldBe(visible);
        return this;
    }
}