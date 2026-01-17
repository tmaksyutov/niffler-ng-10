package guru.qa.niffler.service;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class GhApiClient {

    private static final Config CFG = Config.getInstance();
    private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.ghUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final GhApi ghApi = retrofit.create(GhApi.class);

    @Nonnull
    @Step("Get issue state for issue number '{issueNumber}'")
    @SneakyThrows
    public String issueState(@Nonnull String issueNumber) {
        @Nullable JsonNode responseBody = ghApi.issue(
                "Bearer " + System.getenv(GH_TOKEN_ENV),
                issueNumber
        ).execute().body();

        JsonNode response = requireNonNull(responseBody, "Response body is null");
        JsonNode stateNode = response.get("state");
        return requireNonNull(stateNode, "State field is missing in response").asText();
    }
}