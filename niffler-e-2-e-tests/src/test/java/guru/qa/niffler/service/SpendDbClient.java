package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.sql.Connection;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = new CategoryDaoJdbc(connection).create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(new SpendDaoJdbc(connection).create(spendEntity));
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).create(CategoryEntity.fromJson(category)));
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).update(CategoryEntity.fromJson(category)));
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        return transaction(connection -> {
            return new CategoryDaoJdbc(connection).findByUsernameAndCategoryName(username, categoryName).map(entity -> CategoryJson.fromEntity(entity));
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }

    public Optional<CategoryJson> findById(UUID id) {
        return transaction(connection -> {
            return new CategoryDaoJdbc(connection).findById(id).map(entity -> CategoryJson.fromEntity(entity));

        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }
}