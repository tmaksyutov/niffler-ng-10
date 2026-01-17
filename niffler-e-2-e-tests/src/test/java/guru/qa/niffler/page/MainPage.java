package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage {

    private final SelenideElement statistics = $("#stat");

    private final SpendingTable spendingTable = new SpendingTable();

    private final Header header = new Header();

    @Nonnull
    @Step("Open profile page")
    public ProfilePage goToProfilePage() {
        return header.toProfilePage();
    }

    @Nonnull
    @Step("Open people page")
    public AllPeoplePage goToAllPeoplePage() {
        return header.toAllPeoplesPage();
    }

    @Nonnull
    @Step("Open friends page")
    public FriendsPage goToFriendsPage() {
        return header.toFriendsPage();
    }

    @Nonnull
    @Step("Add a new spending")
    public EditSpendingPage addSpending() {
        return header.addSpendingPage();
    }

    @Nonnull
    @Step("Edit spending")
    public EditSpendingPage editSpending(String description) {
        return spendingTable.editSpending(description);
    }

    @Nonnull
    @Step("Check that table contains '{description}'")
    public MainPage checkThatTableContains(String description) {
        spendingTable.checkTableContains(description);
        return this;
    }

    @Nonnull
    @Step("Check that page loaded")
    public MainPage checkMainPageIsLoaded() {
        statistics.shouldBe(visible);
        return this;
    }
}