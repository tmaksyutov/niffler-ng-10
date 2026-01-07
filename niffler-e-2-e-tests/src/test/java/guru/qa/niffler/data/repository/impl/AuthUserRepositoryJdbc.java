package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private static final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private static final AuthAuthorityDao authorityDao = new AuthAuthorityDaoJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.authJdbcUrl());

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity result = authUserDao.create(user);
            for (AuthorityEntity authority : user.getAuthorities()) {
                authorityDao.create(authority);
            }
            return result;
        });
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        return authUserDao.update(user);
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return authUserDao.findById(id).map(userEntity -> {
            userEntity.setAuthorities(authorityDao.findByUserId((id)));
            return userEntity;
        });
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        return authUserDao.findByUsername(username).map(userEntity -> {
            userEntity.setAuthorities(authorityDao.findByUserId((userEntity.getId())));
            return userEntity;
        });
    }

    @Override
    public void delete(AuthUserEntity user) {
        xaTransactionTemplate.execute(() -> {
            for (AuthorityEntity authority : user.getAuthorities()) {
                authorityDao.delete(authority);
            }
            authUserDao.delete(user);
            return user;
        });
    }
}