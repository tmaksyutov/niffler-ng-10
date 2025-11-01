package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.WebTest;
import guru.qa.niffler.model.CategoryJson;
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

    @User(username = "tmaksyutov",
            categories = {@Category()})
    @Test
    void activeCategoryShouldPresentInCategoryList(CategoryJson category) {
        loginPage
                .login("tmaksyutov", "12345")
                .goToProfilePage()
                .checkProfilePageIsLoaded()
                .checkCategoryIsDisplayed(category.name());
    }

    @User(username = "tmaksyutov",
            categories = {@Category(archived = true)})
    @Test
    void archivedCategoryShouldNotBePresentedInActiveCategoryList(CategoryJson category) {
        loginPage
                .login("tmaksyutov", "12345")
                .goToProfilePage()
                .checkProfilePageIsLoaded()
                .checkCategoryIsNotDisplayed(category.name());
    }

    @User(username = "tmaksyutov",
            categories = {@Category(archived = true)})
    @Test
    void archivedCategoryShouldBePresentedInArchivedList(CategoryJson category) {
        loginPage
                .login("tmaksyutov", "12345")
                .goToProfilePage()
                .checkProfilePageIsLoaded()
                .checkArchivedCategoryExists(category.name());
    }
}