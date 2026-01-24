package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    private final SelenideElement
            avatar = $(".MuiAvatar-root"),
            username = $("#username"),
            nameInput = $("#name"),
            saveChangesBtn = $("button[type='submit']"),
            categoryInput = $("input[name='category']"),
            archivedSwitcher = $(".MuiSwitch-input");

    private final ElementsCollection
            categories = $$(".MuiChip-filled.MuiChip-colorPrimary"),
            categoriesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

    private final Header header = new Header();

    @Nonnull
    @Step("Add new category '{category}'")
    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    @Nonnull
    @Step("Check category is displayed: '{category}'")
    public ProfilePage checkCategoryIsDisplayed(String category) {
        categories.find(text(category)).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Check category is not displayed: '{category}'")
    public ProfilePage checkCategoryIsNotDisplayed(String category) {
        categories.find(text(category)).shouldNot(visible);
        return this;
    }

    @Nonnull
    @Step("Check archived category exists: '{category}'")
    public ProfilePage checkArchivedCategoryExists(String category) {
        archivedSwitcher.click();
        categoriesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Check the profile page is loaded")
    public ProfilePage checkProfilePageIsLoaded() {
        avatar.shouldBe(visible);
        username.shouldBe(visible);
        nameInput.shouldBe(visible);
        saveChangesBtn.shouldBe(visible);
        categoryInput.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Set name: '{name}'")
    public ProfilePage setNewName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    @Step("Save changes")
    public ProfilePage saveChanges() {
        saveChangesBtn.click();
        return this;
    }

    @Nonnull
    @Step("Return to main page")
    public MainPage returnToMainPage() {
        return header.toMainPage();
    }

    @Nonnull
    @Step("Check that name is '{name}'")
    public ProfilePage checkUserName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }
}