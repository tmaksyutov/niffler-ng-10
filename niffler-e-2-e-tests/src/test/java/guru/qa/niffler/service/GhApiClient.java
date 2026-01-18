package guru.qa.niffler.service;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public final class GhApiClient extends RestClient {

    private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

    private final GhApi ghApi;

    public GhApiClient() {
        super(CFG.ghUrl());
        this.ghApi = create(GhApi.class);
    }

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