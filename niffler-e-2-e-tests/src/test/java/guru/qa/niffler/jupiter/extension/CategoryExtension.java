package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendClient spendClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(
                annotation -> {
                    if (annotation.categories().length > 0) {
                        CategoryJson created = spendClient.createCategory(
                                new CategoryJson(
                                        null,
                                        RandomDataUtils.randomCategoryName(),
                                        annotation.username(),
                                        false
                                )
                        );
                        if (annotation.categories()[0].archived()) created = spendClient.updateCategory(
                                new CategoryJson(
                                        created.id(),
                                        created.name(),
                                        created.username(),
                                        true
                                ));
                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                created
                        );
                    }
                }
        );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(annotation -> {
            if (annotation.categories().length > 0) {
                CategoryJson category = context.getStore(NAMESPACE)
                        .get(context.getUniqueId(), CategoryJson.class);
                category = spendClient
                        .findCategoryByNameAndUsername(category.name(), category.username())
                        .orElseThrow();
                if (!category.archived()) {
                    spendClient.updateCategory(
                            new CategoryJson(
                                    category.id(),
                                    category.name(),
                                    category.username(),
                                    true
                            ));
                }
            }
        });
    }
}