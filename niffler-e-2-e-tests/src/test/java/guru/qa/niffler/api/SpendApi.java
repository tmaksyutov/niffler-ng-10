package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Date;
import java.util.List;

public interface SpendApi {

    @GET("internal/spends/{id}")
    Call<SpendJson> getSpendById(
            @Query("username") String username,
            @Path("id") String id
    );

    @POST("internal/spends/add")
    Call<SpendJson> createSpend(@Body SpendJson spend);

    @GET("internal/spends/all")
    Call<List<SpendJson>> getAllSpends(
            @Query("username") String username,
            @Query("filterCurrency") CurrencyValues filterCurrency,
            @Query("from") Date from,
            @Query("to") Date to
    );

    @PATCH("internal/spends/edit")
    Call<SpendJson> editSpend(@Body SpendJson spend);

    @DELETE("internal/spends/remove")
    Call<Void> deleteSpend(
            @Query("username") String username,
            @Query("ids") List<String> ids
    );

    @GET("internal/categories/all")
    Call<List<CategoryJson>> getAllCategories(
            @Query("username") String username,
            @Query("excludeArchived") boolean excludeArchived
    );

    @POST("internal/categories/add")
    Call<CategoryJson> addCategory(@Body CategoryJson category);

    @PATCH("internal/categories/update")
    Call<CategoryJson> updateCategory(@Body CategoryJson category);
}