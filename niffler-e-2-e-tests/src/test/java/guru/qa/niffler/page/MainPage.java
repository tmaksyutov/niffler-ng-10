package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    private final ElementsCollection tableRows = $$("#spendings tr");
    private final SelenideElement spendings = $("#spendings");
    private final SelenideElement statistics = $("#stat");
    private final SelenideElement personIcon = $("[data-testid='PersonIcon']");
    private final SelenideElement profileLink = $("a[href='/profile']");
    private final SelenideElement friendsLink = $("a[href='/people/friends']");
    private final SelenideElement allPeopleLink = $("a[href='/people/all']");


    private final SelenideElement spendingTable = $("#spendings"),
            menuBtn = $("button[aria-label='Menu']"),
            menu = $("ul[role='menu']");

    public final ElementsCollection menuItems = menu.$$("li");

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
}