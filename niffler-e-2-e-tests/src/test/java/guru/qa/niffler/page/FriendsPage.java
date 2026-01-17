package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class FriendsPage {
    private final ElementsCollection
            friendsTable = $$("#friends tr"),
            requestsTable = $$("#requests tr");
    private final SelenideElement
            allPeopleTab = $("a[href='/people/all']"),
            nextPageButton = $("#page-next"),
            previousPageButton = $("#page-prev"),
            confirmDeclineButton = $(".MuiDialogActions-root button:nth-child(2)");

    private final By acceptButton = By.xpath(".//button[text()='Accept']");
    private final By declineButton = By.xpath(".//button[text()='Decline']");
    private final By unfriendButton = By.xpath(".//button[text()='Unfriend']");

    private final SearchField searchField = new SearchField();

    @Nonnull
    @Step("Switch to 'All People' tab")
    public AllPeoplePage switchToAllPeopleTab() {
        allPeopleTab.click();
        return new AllPeoplePage();
    }

    @Nonnull
    @Step("Check that requests table contains incoming friend request from '{friendName}'")
    public FriendsPage requestsTableShouldContainIncomeFriend(String friendName) {
        searchField.search(friendName);
        SelenideElement friendRow = requestsTable.find(text(friendName)).shouldBe(visible);
        friendRow.find(acceptButton).shouldBe(visible);
        friendRow.find(declineButton).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Check that friends table contains friend '{friendName}'")
    public FriendsPage friendsTableShouldContainFriend(String friendName) {
        searchField.search(friendName);
        friendsTable.find(text(friendName)).shouldBe(visible)
                .find(unfriendButton).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Check that friends table is empty")
    public FriendsPage friendsTableShouldBeEmpty() {
        friendsTable.shouldBe(empty);
        return this;
    }

    @Nonnull
    @Step("Accept invitation from friend '{friendName}'")
    public FriendsPage acceptInvitationFrom(String friendName) {
        requestsTable.find(text(friendName))
                .find(acceptButton).click();
        return this;
    }

    @Nonnull
    @Step("Decline invitation from friend '{friendName}'")
    public FriendsPage declineInvitationFrom(String friendName) {
        requestsTable.find(text(friendName))
                .find(declineButton).click();
        confirmDeclineButton.click();
        searchField.clearIfNotEmpty();
        return this;
    }

    @Nonnull
    @Step("Go to next page")
    public FriendsPage nextPage() {
        nextPageButton.click();
        return this;
    }

    @Nonnull
    @Step("Go to previous page")
    public FriendsPage previousPage() {
        previousPageButton.click();
        return this;
    }
}