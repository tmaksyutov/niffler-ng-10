package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class AllPeoplePage extends BasePage<AllPeoplePage> {
    private final ElementsCollection
            allPeopleTable = $$("#all tr");
    private final SelenideElement
            friendsTab = $("a[href='/people/friends']"),
            searchInput = $("[aria-label=\"search\"]"),
            pageNextButton = $("#page-next"),
            pagePreviousButton = $("#page-prev"),
            allPeopleTab = $x("//a[@href='/people/all']");
    private final String
            addFriendButton = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Add friend']";

    private final By requestLabel = By.cssSelector(".MuiChip-label");
    private final SearchField searchField = new SearchField();

    @Nonnull
    @Step("Switch to 'Friends' tab")
    public FriendsPage switchToFriendsTab() {
        friendsTab.click();
        return new FriendsPage();
    }

    @Nonnull
    @Step("Search for user with value: {searchValue}")
    public AllPeoplePage search(String searchValue) {
        searchInput.val(searchValue);
        return this;
    }

    @Nonnull
    @Step("Add user '{userName}' as friend")
    public AllPeoplePage addFriend(String userName) {
        allPeopleTable.findBy(text(userName))
                .shouldBe(visible)
                .find(byXpath(addFriendButton))
                .click();
        return this;
    }

    @Nonnull
    @Step("Check that user '{userName}' is present in the table")
    public AllPeoplePage checkUser(String userName) {
        allPeopleTable.findBy(text(userName))
                .shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Check that all people table contains waiting answer from friend '{friendName}'")
    public AllPeoplePage allPeoplesTableShouldContainWaitingAnswerFromFriend(String friendName) {
        allPeopleTab.click();
        searchField.search(friendName);
        allPeopleTable.find(text(friendName)).shouldBe(visible)
                .find(requestLabel).shouldBe(visible)
                .shouldHave(text("Waiting..."));
        return this;
    }

    @Nonnull
    @Step("Go to next page")
    public AllPeoplePage nextPage() {
        pageNextButton.click();
        return this;
    }

    @Nonnull
    @Step("Go to previous page")
    public AllPeoplePage previousPage() {
        pagePreviousButton.click();
        return this;
    }
}