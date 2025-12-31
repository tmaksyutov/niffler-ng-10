package guru.qa.niffler.test.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepositoryTest {
    private static final Config CFG = Config.getInstance();

    @Test
    void testFindAuthUserWithAuthorities() {
        AuthUserRepository repository = new AuthUserRepositoryJdbc();
        JdbcTransactionTemplate txTemplate = new JdbcTransactionTemplate(CFG.authJdbcUrl());

        UUID userId = txTemplate.execute(() -> {
            AuthUserEntity user = new AuthUserEntity();
            user.setUsername(RandomDataUtils.randomUsername());
            user.setPassword("password");
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);

            AuthorityEntity authority1 = new AuthorityEntity();
            authority1.setAuthority(Authority.read);
            authority1.setUser(user);

            AuthorityEntity authority2 = new AuthorityEntity();
            authority2.setAuthority(Authority.write);
            authority2.setUser(user);

            user.setAuthorities(java.util.Arrays.asList(authority1, authority2));

            return repository.create(user).getId();
        });

        Optional<AuthUserEntity> foundUser = txTemplate.execute(() -> repository.findById(userId));

        assertTrue(foundUser.isPresent());
        assertEquals(2, foundUser.get().getAuthorities().size());

        foundUser.get().getAuthorities().forEach(authority -> {
            assertNotNull(authority.getUser());
            assertEquals(userId, authority.getUser().getId());
        });
    }

    @Test
    void testCreateUserRepository() {
        UserRepository repository = new UserRepositorySpringJdbc();
        JdbcTransactionTemplate txTemplate = new JdbcTransactionTemplate(CFG.userdataJdbcUrl());

        UserEntity user = txTemplate.execute(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setUsername(RandomDataUtils.randomUsername());
            newUser.setCurrency(CurrencyValues.RUB);
            newUser.setFirstname(RandomDataUtils.randomName());
            newUser.setSurname(RandomDataUtils.randomSurname());

            return repository.create(newUser);
        });

        Optional<UserEntity> foundUser = txTemplate.execute(() -> repository.findByUsername(user.getUsername()));

        assertTrue(foundUser.isPresent());
        assertEquals(user.getUsername(), foundUser.get().getUsername());
    }
}