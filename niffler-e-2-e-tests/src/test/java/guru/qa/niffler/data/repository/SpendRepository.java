package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.SpendRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.SpendRepositorySpringJdbc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendRepository {

    @Nonnull
    static SpendRepository getInstance() {
        return switch (System.getProperty("repository", "jpa")) {
            case "jpa" -> new SpendRepositoryHibernate();
            case "jdbc" -> new SpendRepositoryJdbc();
            case "sjdbc" -> new SpendRepositorySpringJdbc();
            default -> throw new IllegalArgumentException("Unknown repository type: " + System.getProperty("repository"));
        };
    }

    @Nonnull
    SpendEntity create(SpendEntity spend);

    @Nonnull
    SpendEntity update(SpendEntity spend);

    @Nonnull
    Optional<SpendEntity> findSpendById(UUID id);

    @Nonnull
    List<SpendEntity> findAllByUsername(String username);

    @Nonnull
    List<SpendEntity> findAll();

    void deleteSpend(SpendEntity spend);

    @Nonnull
    CategoryEntity createCategory(CategoryEntity category);

    @Nonnull
    CategoryEntity updateCategory(CategoryEntity category);

    @Nonnull
    Optional<CategoryEntity> findCategoryById(UUID id);

    @Nonnull
    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

    void deleteCategory(CategoryEntity category);
}