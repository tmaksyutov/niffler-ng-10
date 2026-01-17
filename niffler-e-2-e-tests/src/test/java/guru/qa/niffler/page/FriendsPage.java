package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
    private final ElementsCollection
            friendsTableRows = $$("#friends tr"),
            requestsTableRows = $$("#requests tr");
    private final SelenideElement
            allPeopleTab = $("a[href='/people/all']"),
            searchInput = $("[aria-label=\"search\"]"),
            nextPageButton = $("#page-next"),
            previousPageButton = $("#page-prev");
    private final String
            acceptButton = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Accept']",
            declineButton = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Decline']";

    public AllPeoplePage switchToAllPeopleTab() {
        allPeopleTab.click();
        return new AllPeoplePage();
    }

    public FriendsPage search(String searchValue) {
        searchInput.val(searchValue);
        return this;
    }

    public FriendsPage acceptRequest(String friendName) {
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible)
                .find(byXpath(acceptButton))
                .click();
        return this;
    }

    public FriendsPage declineRequest(String friendName) {
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible)
                .find(byXpath(declineButton))
                .click();
        return this;
    }

    public FriendsPage checkFriend(String friendName) {
        searchInput.val(friendName).sendKeys(Keys.ENTER);
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible);
        return this;
    }

    public FriendsPage checkFriendsEmpty() {
        friendsTableRows.first().shouldNot(exist);
        return this;
    }

    public FriendsPage checkRequest(String friendName) {
        searchInput.val(friendName).sendKeys(Keys.ENTER);
        requestsTableRows.findBy(text(friendName))
                .shouldBe(visible);
        return this;
    }

    public FriendsPage nextPage() {
        nextPageButton.click();
        return this;
    }

    public FriendsPage previousPage() {
        previousPageButton.click();
        return this;
    }
}