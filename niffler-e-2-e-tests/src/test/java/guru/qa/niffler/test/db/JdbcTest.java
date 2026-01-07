package guru.qa.niffler.test.db;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JdbcTest {


    static UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(strings = {
            "valentin-31"
    })
    @ParameterizedTest
    void springJdbcTest(String uname) {
        UserJson user = usersDbClient.createUser(
                uname,
                "12345"
        );
        usersDbClient.createIncomeInvitation(user, 1);
        usersDbClient.createOutcomeInvitation(user, 1);
    }

    @Test
    void createCategoryTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        CategoryJson category = spendDbClient.createCategory(
                new CategoryJson(
                        null,
                        "Test category-13",
                        "username-13",
                        false
                )
        );
        System.out.println(category);
    }
}