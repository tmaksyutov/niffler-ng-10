package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.user.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserDao {

    @Nonnull
    UserEntity create(UserEntity user);

    @Nonnull
    UserEntity update(UserEntity user);

    @Nonnull
    Optional<UserEntity> findById(UUID id);

    @Nonnull
    Optional<UserEntity> findByUsername(String username);

    @Nonnull
    List<UserEntity> findAll();

    void delete(UserEntity user);
}