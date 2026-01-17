package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {

    private final SelenideElement descriptionInput = $("#description"),
            saveBtn = $("#save"),
            amountInput = $("#amount"),
            categoryInput = $("#category");

    private final Calendar calendar = new Calendar();

    @Nonnull
    @Step("Set new spending description: '{description}'")
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.setValue(description);
        return this;
    }

    @Nonnull
    @Step("Set new spending amount: {amount}")
    public EditSpendingPage setNewSpendingAmount(long amount) {
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Nonnull
    @Step("Set new spending category: '{category}'")
    public EditSpendingPage setNewSpendingCategory(String category) {
        categoryInput.setValue(category);
        return this;
    }

    @Nonnull
    @Step("Set new spending date")
    public EditSpendingPage setNewSpendingDate(Date date) {
        calendar.selectDateInCalendar(date);
        return this;
    }

    @Nonnull
    @Step("Save spending")
    public MainPage save() {
        saveBtn.click();
        return new MainPage();
    }
}