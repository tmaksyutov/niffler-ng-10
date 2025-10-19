package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class DataUtils {

    private static final Faker faker = new Faker();

    public static String getRandomCategoryName() {
        return faker.commerce().department();
    }

    public static String getRandomUserName() {
        return faker.name().username();
    }

    public static String getRandomPassword() {
        return faker.internet().password(4, 10, true, true, true);
    }
}