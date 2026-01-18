package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public final class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = SpendRepository.getInstance();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Nonnull
    @Step("Create spend in database")
    @Override
    public SpendJson createSpend(SpendJson spend) {
        return xaTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            return SpendJson.fromEntity(
                    spendRepository.create(spendEntity)
            );
        });
    }

    @Nonnull
    @Step("Create category in database")
    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return xaTxTemplate.execute(() -> CategoryJson.fromEntity(
                        spendRepository.createCategory(
                                CategoryEntity.fromJson(category)
                        )
                )
        );
    }

    @Nonnull
    @Step("Update category in database")
    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return xaTxTemplate.execute(() -> CategoryJson.fromEntity(
                        spendRepository.updateCategory(
                                CategoryEntity.fromJson(category)
                        )
                )
        );
    }

    @Nonnull
    @Step("Find category by name '{categoryName}' for user '{username}' in database")
    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        return xaTxTemplate.execute(() -> {
            Optional<CategoryEntity> category = spendRepository.findCategoryByUsernameAndCategoryName(username, categoryName);
            return category.map(CategoryJson::fromEntity);
        });
    }
}