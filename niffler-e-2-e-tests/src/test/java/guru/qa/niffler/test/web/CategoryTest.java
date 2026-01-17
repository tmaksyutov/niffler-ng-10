package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WebTest
public class CategoryTest {

    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    @User(
            categories = {@Category()})
    @Test
    void activeCategoryShouldPresentInCategoryList(UserJson user) {
        loginPage
                .login(user.username(), "12345")
                .goToProfilePage()
                .checkProfilePageIsLoaded()
                .checkCategoryIsDisplayed(user.testData().categories().getFirst().name());
    }

    @User(
            categories = {@Category(archived = true)})
    @Test
    void archivedCategoryShouldNotBePresentedInActiveCategoryList(UserJson user) {
        loginPage
                .login(user.username(), "12345")
                .goToProfilePage()
                .checkProfilePageIsLoaded()
                .checkCategoryIsNotDisplayed(user.testData().categories().getFirst().name());
    }

    @User(
            categories = {@Category(archived = true)})
    @Test
    void archivedCategoryShouldBePresentedInArchivedList(UserJson user) {
        loginPage
                .login(user.username(), "12345")
                .goToProfilePage()
                .checkProfilePageIsLoaded()
                .checkArchivedCategoryExists(user.testData().categories().getFirst().name());
    }
}