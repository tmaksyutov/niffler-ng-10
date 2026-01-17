package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendRepository {

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