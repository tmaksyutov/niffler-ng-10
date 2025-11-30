package guru.qa.niffler.test.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChainedTxManagerTest {
    private static final Config CFG = Config.getInstance();

    private DataSourceTransactionManager authManager;
    private DataSourceTransactionManager userManager;
    private ChainedTransactionManager chainedTxManager;
    private TransactionTemplate txTemplate;

    private AuthUserDaoJdbc authUserDao;
    private UserDaoJdbc userDao;
    private AuthAuthorityDaoJdbc authAuthorityDao;

    @BeforeEach
    void setUp() {
        authManager = new DataSourceTransactionManager(
                DataSources.dataSource(CFG.authJdbcUrl())
        );
        userManager = new DataSourceTransactionManager(
                DataSources.dataSource(CFG.userdataJdbcUrl())
        );
        chainedTxManager = new ChainedTransactionManager(authManager, userManager);
        txTemplate = new TransactionTemplate(chainedTxManager);

        authUserDao = new AuthUserDaoJdbc();
        userDao = new UserDaoJdbc();
        authAuthorityDao = new AuthAuthorityDaoJdbc();
    }

    private AuthUserEntity createTestAuthUser() {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setEnabled(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setAccountNonExpired(true);
        authUser.setUsername(RandomDataUtils.randomUsername());
        authUser.setPassword("12345");
        return authUser;
    }

    private UserEntity createTestUserData(String username) {
        UserEntity userData = new UserEntity();
        userData.setFullname("Testov Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testov");
        userData.setUsername(username);
        userData.setCurrency(CurrencyValues.RUB);
        return userData;
    }

    private AuthorityEntity createTestAuthority(AuthUserEntity user) {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);
        authority.setUser(user);
        return authority;
    }

    @Test
    void successTransactionTest() {
        // Arrange
        AuthUserEntity authUser = createTestAuthUser();
        UserEntity userData = createTestUserData(authUser.getUsername());
        AuthorityEntity authority = createTestAuthority(authUser);

        // Act & Assert
        txTemplate.execute(status -> {
            try (Connection authConn = authManager.getDataSource().getConnection();
                 Connection userConn = userManager.getDataSource().getConnection()) {

                authUserDao.create(authUser);
                System.out.println("Создали юзера в auth: " + authUser.getUsername());

                userDao.create(userData);
                System.out.println("Создали юзера в userdata");

                authAuthorityDao.create(authority);
                System.out.println("Создали authority в auth");

                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void failTransactionTest() {
        // Arrange
        AuthUserEntity authUser = createTestAuthUser();
        UserEntity userData = createTestUserData(authUser.getUsername());

        // Создаем authority с невалидным пользователем для провокации ошибки
        AuthorityEntity invalidAuthority = new AuthorityEntity();
        invalidAuthority.setAuthority(Authority.read);
        invalidAuthority.setUser(new AuthUserEntity()); // Невалидный пользователь

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                txTemplate.execute(status -> {
                    try (Connection authConn = authManager.getDataSource().getConnection();
                         Connection userConn = userManager.getDataSource().getConnection()) {

                        authUserDao.create(authUser);
                        System.out.println("Создали юзера в auth: " + authUser.getUsername());

                        userDao.create(userData);
                        System.out.println("Создали юзера в userdata");

                        // Эта операция должна упасть с ошибкой
                        authAuthorityDao.create(invalidAuthority);
                        System.out.println("Создали authority в auth");

                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    @Test
    void transactionRollbackOnExceptionTest() {
        // Arrange
        AuthUserEntity authUser = createTestAuthUser();
        UserEntity userData = createTestUserData(authUser.getUsername());
        AuthorityEntity authority = createTestAuthority(authUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                txTemplate.execute(status -> {
                    try (Connection authConn = authManager.getDataSource().getConnection();
                         Connection userConn = userManager.getDataSource().getConnection()) {

                        authUserDao.create(authUser);
                        System.out.println("Создали юзера в auth: " + authUser.getUsername());

                        userDao.create(userData);
                        System.out.println("Создали юзера в userdata");

                        // Искусственно вызываем исключение после успешных операций
                        throw new RuntimeException("Искусственное исключение для отката транзакции");

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }
}