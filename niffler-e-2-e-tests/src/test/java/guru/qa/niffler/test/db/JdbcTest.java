package guru.qa.niffler.test.db;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;

import java.sql.Date;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

public class JdbcTest {

    @Test
    void createUserTest() {
        UsersDbClient userDbClient = new UsersDbClient();
        UserJson user = userDbClient.create(
                new UserJson(
                        null,
                        "valentino-1",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createCategoryAndSpendTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        // Создаем категорию
        CategoryJson category = spendDbClient.createCategory(
                new CategoryJson(
                        null,
                        "Test category-1",
                        "username-1",
                        false
                )
        );
        System.out.println(category);

        // Создаем трату по категории
        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(Calendar.getInstance().getTime().getTime()),
                        category,
                        CurrencyValues.USD,
                        100.50,
                        "Test spend-1",
                        "username-1"
                )
        );
        System.out.println(spend);
    }
}