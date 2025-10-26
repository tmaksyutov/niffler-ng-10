package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    private final ElementsCollection tableRows = $$("#spendings tr");
    private final SelenideElement
            personIcon = $("[data-testid='PersonIcon']"),
            friendsLink = $("a[href='/people/friends']"),
            allPeopleLink = $("a[href='/people/all']"),
            spendingTable = $("#spendings"),
            menuBtn = $("button[aria-label='Menu']"),
            menu = $("ul[role='menu']");

    private final ElementsCollection menuItems = menu.$$("li");

    public ProfilePage goToProfilePage() {
        menuBtn.click();
        menuItems.find(text("Profile")).click();
        return new ProfilePage();
    }

    public EditSpendingPage editSpending(String description) {
        tableRows.find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public MainPage checkThatTableContains(String description) {
        tableRows.find(text(description)).should(visible);
        return this;
    }

    public MainPage checkMainPageIsLoaded() {
        spendingTable.shouldBe(visible);
        menuBtn.shouldBe(visible);
        return this;
    }

    public AllPeoplePage goToAllPeoplePage() {
        personIcon.click();
        allPeopleLink.click();
        return new AllPeoplePage();
    }

    public FriendsPage goToFriendsPage() {
        personIcon.click();
        friendsLink.click();
        return new FriendsPage();
    }
}