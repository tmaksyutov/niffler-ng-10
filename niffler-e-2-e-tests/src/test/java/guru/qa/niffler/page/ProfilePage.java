package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {

    private final SelenideElement
            avatar = $(".MuiAvatar-root"),
            username = $("#username"),
            name = $("#name"),
            saveChangesBtn = $("button[type='submit']"),
            categoryInput = $("input[name='category']"),
            archivedSwitcher = $(".MuiSwitch-input");

    private final ElementsCollection
            categories = $$(".MuiChip-filled.MuiChip-colorPrimary"),
            categoriesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

    @Step("Add new category '{category}'")
    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    @Step("Check category: '{category}'")
    public ProfilePage checkCategoryIsDisplayed(String category) {
        categories.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Check category: '{category}'")
    public boolean checkCategoryIsNotDisplayed(String category) {
        categories.find(text(category)).shouldNot(visible);
        return true;
    }

    @Step("Check archived category: '{category}'")
    public ProfilePage checkArchivedCategoryExists(String category) {
        archivedSwitcher.click();
        categoriesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Check the profile page is loaded")
    public ProfilePage checkProfilePageIsLoaded() {
        avatar.shouldBe(visible);
        username.shouldBe(visible);
        name.shouldBe(visible);
        saveChangesBtn.shouldBe(visible);
        categoryInput.shouldBe(visible);
        return this;
    }
}