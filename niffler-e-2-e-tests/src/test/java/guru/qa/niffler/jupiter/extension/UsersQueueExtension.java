package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.UserType;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(String username, String password, String friend, String outcome, String income) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();


    static {
        EMPTY_USERS.add(new StaticUser("EmptyTestUser1", "12345", null, null, null));
        EMPTY_USERS.add(new StaticUser("EmptyTestUser2", "12345", null, null, null));

        WITH_FRIEND_USERS.add(new StaticUser("WFTestUser1", "12345", "WFTestUser2", null, null));
        WITH_FRIEND_USERS.add(new StaticUser("WFTestUser2", "12345", "WFTestUser1", null, null));

        WITH_INCOME_REQUEST_USERS.add(new StaticUser("IRTestUser1", "12345", null, null, "ORTestUser1"));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("IRTestUser2", "12345", null, null, "ORTestUser2"));

        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("ORTestUser1", "12345", null, "IRTestUser1", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("ORTestUser2", "12345", null, "IRTestUser2", null));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .findFirst()
                .map(p -> p.getAnnotation(UserType.class))
                .ifPresent(ut -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = Optional.ofNullable(getQueueByUserType(ut).poll());
                    }
                    Allure.getLifecycle().updateTestCase(testCase ->
                            testCase.setStart(new Date().getTime())
                    );
                    user.ifPresentOrElse(
                            u ->
                                    ((Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                                            .getOrComputeIfAbsent(
                                                    context.getUniqueId(),
                                                    key -> new HashMap<>()
                                            )
                                    ).put(ut, u),
                            () -> {
                                throw new IllegalStateException("Can`t obtain user after 30s.");
                            }
                    );
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Map<UserType, StaticUser> userMap = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), Map.class);
        for (Map.Entry<UserType, StaticUser> entry : userMap.entrySet()) {
            getQueueByUserType(entry.getKey()).add(entry.getValue());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.findAnnotation(UserType.class)
                .map(anno -> {
                    Map<UserType, StaticUser> userMap = extensionContext.getStore(NAMESPACE)
                            .get(extensionContext.getUniqueId(), Map.class);
                    return userMap.get(anno);
                })
                .orElseThrow(
                        () -> new ParameterResolutionException("No @UserType annotation found")
                );
    }

    private Queue<StaticUser> getQueueByUserType(UserType userType) {
        switch (userType.value()) {
            case WITHOUT_FRIEND -> {
                return EMPTY_USERS;
            }
            case WITH_FRIEND -> {
                return WITH_FRIEND_USERS;
            }
            case WITH_INCOME_REQUEST -> {
                return WITH_INCOME_REQUEST_USERS;
            }
            case WITH_OUTCOME_REQUEST -> {
                return WITH_OUTCOME_REQUEST_USERS;
            }
            default -> throw new IllegalStateException("Unexpected value: " + userType);
        }
    }
}