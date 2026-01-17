package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    @Nonnull
    @Override
    public AuthUserEntity create(@Nonnull AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Nonnull
    @Override
    public AuthUserEntity update(@Nonnull AuthUserEntity user) {
        entityManager.joinTransaction();
        return entityManager.merge(user);
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(
                entityManager.find(AuthUserEntity.class, id)
        );
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findByUsername(@Nonnull String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", AuthUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(@Nonnull AuthUserEntity user) {
        entityManager.joinTransaction();
        if (!entityManager.contains(user)) {
            user = entityManager.merge(user);
        }
        entityManager.remove(user);
    }
}