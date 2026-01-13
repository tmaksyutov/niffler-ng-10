package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class SpendRepositoryHibernate implements SpendRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Override
    public SpendEntity create(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.merge(spend);
        return spend;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return Optional.ofNullable(entityManager.find(SpendEntity.class, id));
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        return entityManager.createQuery("SELECT s FROM SpendEntity s WHERE s.username = :username", SpendEntity.class)
                .setParameter("username", username)
                .getResultList();
    }

    @Override
    public List<SpendEntity> findAll() {
        return entityManager.createQuery("SELECT s FROM SpendEntity s", SpendEntity.class)
                .getResultList();
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        entityManager.joinTransaction();
        if (!entityManager.contains(spend)) {
            spend = entityManager.merge(spend);
        }
        entityManager.remove(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.merge(category);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(entityManager.find(CategoryEntity.class, id));
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
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
    public void deleteCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        if (!entityManager.contains(category)) {
            category = entityManager.merge(category);
        }
        entityManager.remove(category);
    }
}