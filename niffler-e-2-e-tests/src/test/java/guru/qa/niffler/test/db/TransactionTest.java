package guru.qa.niffler.test.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class TransactionTest {
    private static final Config CFG = Config.getInstance();

    @Test
    public void successTransactionTest() {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setEnabled(true);
        authUserEntity.setCredentialsNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setUsername(RandomDataUtils.randomUsername());
        authUserEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);
        UserEntity userData = new UserEntity();
        userData.setFullname("Testov Test Testovich");
        userData.setFirstname("Test");
        userData.setSurname("Testov");
        userData.setUsername(authUserEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);

        xaTransaction(Connection.TRANSACTION_REPEATABLE_READ, new Databases.XaFunction<>(connection -> {
                    authUserEntity.setId(new AuthUserDaoJdbc(connection).create(authUserEntity).getId());
                    System.out.println("Создан юзер в auth " + authUserEntity.getUsername());
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    authority.setUser(authUserEntity);
                    new AuthAuthorityDaoJdbc(connection).create(authority);
                    System.out.println("Создан authority в auth");
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    new UserDaoJdbc(connection).create(userData);
                    System.out.println("Создан юзер в userdata");
                    return null;
                }, CFG.userdataJdbcUrl()));
    }

    @Test
    public void failTransactionTest() {
        AuthUserEntity userAuthEntity = new AuthUserEntity();
        userAuthEntity.setEnabled(true);
        userAuthEntity.setCredentialsNonExpired(true);
        userAuthEntity.setAccountNonLocked(true);
        userAuthEntity.setAccountNonExpired(true);
        userAuthEntity.setUsername(RandomDataUtils.randomUsername());
        userAuthEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testov Test Testovich");
        userData.setFirstname("Test");
        userData.setSurname("Testov");
        userData.setUsername(userAuthEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);

        xaTransaction(Connection.TRANSACTION_REPEATABLE_READ, new Databases.XaFunction<>(connection -> {
                    userAuthEntity.setId(new AuthUserDaoJdbc(connection).create(userAuthEntity).getId());
                    System.out.println("Создан юзер в auth " + userAuthEntity.getUsername());
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    authority.setUser(userAuthEntity);
                    new AuthAuthorityDaoJdbc(connection).create(authority);
                    System.out.println("Создан authority в auth");
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    new UserDaoJdbc(connection).create(userData);
                    System.out.println("Создан юзер в userdata");
                    return null;
                }, CFG.userdataJdbcUrl()));
    }
}