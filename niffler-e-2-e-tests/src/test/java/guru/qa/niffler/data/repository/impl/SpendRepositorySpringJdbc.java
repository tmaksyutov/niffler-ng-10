package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {
    private static final Config CFG = Config.getInstance();
    private static final SpendDao spendDao = new SpendDaoSpringJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    public SpendEntity create(SpendEntity spend) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity resultSpend = spendDao.create(spend);
            if (spend.getCategory() != null) {
                categoryDao.findById(spend.getCategory().getId())
                        .orElseGet(() -> categoryDao.create(spend.getCategory()));
            }
            return resultSpend;
        });
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        return spendDao.update(spend);
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return spendDao.findById(id).map(spendEntity -> {
            spendEntity.setCategory(categoryDao.findById(spendEntity.getCategory().getId()).get());
            return spendEntity;
        });
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        List<SpendEntity> resultList = spendDao.findAllByUsername(username);
        for (SpendEntity spend : resultList) {
            spend.setCategory(categoryDao.findById(spend.getCategory().getId()).get());
        }
        return resultList;
    }

    @Override
    public List<SpendEntity> findAll() {
        return spendDao.findAll();
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        spendDao.delete(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        return categoryDao.update(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return categoryDao.findByUsernameAndCategoryName(username, categoryName);
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        categoryDao.delete(category);
    }
}