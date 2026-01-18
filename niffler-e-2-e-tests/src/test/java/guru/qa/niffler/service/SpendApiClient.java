package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public final class SpendApiClient extends RestClient implements SpendClient {

    private final SpendApi spendApi;

    public SpendApiClient() {
        super(CFG.spendUrl());
        this.spendApi = create(SpendApi.class);
    }

    @Nonnull
    @Step("Create spend")
    @Override
    public SpendJson createSpend(@Nonnull SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.createSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return response.body();
    }

    @Nonnull
    @Step("Create category")
    @Override
    public CategoryJson createCategory(@Nonnull CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Nonnull
    @Step("Find category by name '{categoryName}' for user '{username}'")
    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(@Nonnull String categoryName, @Nonnull String username) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getAllCategories(username, false).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return Optional.ofNullable(response.body())
                .orElse(List.of())
                .stream()
                .filter(c -> categoryName.equals(c.name()))
                .findFirst();
    }

    @Nullable
    @Step("Find spend by username '{username}' and ID '{id}'")
    public SpendJson findSpendByUsernameAndId(@Nonnull String username, @Nonnull String id) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpendById(username, id).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Nonnull
    @Step("Get all spends for user '{username}' with currency filter '{filterCurrency}' from '{from}' to '{to}'")
    public List<SpendJson> getAllSpends(
            @Nonnull String username,
            @Nullable CurrencyValues filterCurrency,
            @Nullable Date from,
            @Nullable Date to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getAllSpends(username, filterCurrency, from, to).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body() != null ? response.body() : List.of();
    }

    @Nonnull
    @Step("Edit spend with ID '{spend.id}'")
    public SpendJson editSpend(@Nonnull SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Delete spends for user '{username}' with IDs: {ids}")
    public void deleteSpend(@Nonnull String username, @Nonnull List<String> ids) {
        final Response<Void> response;
        try {
            response = spendApi.deleteSpend(username, ids).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(202, response.code());
    }

    @Nonnull
    @Step("Update category '{category.name}'")
    public CategoryJson updateCategory(@Nonnull CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}