package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class SpendRepositoryHibernate implements SpendRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Nonnull
    @Override
    public SpendEntity create(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Nonnull
    @Override
    public SpendEntity update(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.merge(spend);
        return spend;
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findSpendById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(SpendEntity.class, id));
    }

    @Nonnull
    @Override
    public List<SpendEntity> findAllByUsername(@Nonnull String username) {
        return entityManager.createQuery("SELECT s FROM SpendEntity s WHERE s.username = :username", SpendEntity.class)
                .setParameter("username", username)
                .getResultList();
    }

    @Nonnull
    @Override
    public List<SpendEntity> findAll() {
        return entityManager.createQuery("SELECT s FROM SpendEntity s", SpendEntity.class)
                .getResultList();
    }

    @Override
    public void deleteSpend(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        if (!entityManager.contains(spend)) {
            spend = entityManager.merge(spend);
        }
        entityManager.remove(spend);
    }

    @Nonnull
    @Override
    public CategoryEntity createCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Nonnull
    @Override
    public CategoryEntity updateCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.merge(category);
        return category;
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(CategoryEntity.class, id));
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(
            @Nonnull String username,
            @Nonnull String categoryName
    ) {
        try {
            return Optional.ofNullable(entityManager.createQuery("SELECT c FROM CategoryEntity c WHERE c.username = :username AND c.name  = :name", CategoryEntity.class)
                    .setParameter("username", username)
                    .setParameter("name", categoryName)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        if (!entityManager.contains(category)) {
            category = entityManager.merge(category);
        }
        entityManager.remove(category);
    }
}