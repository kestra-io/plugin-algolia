package io.kestra.plugin.algolia;

import com.google.common.collect.ImmutableMap;
import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.models.property.Property;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import jakarta.inject.Inject;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisabledIf(
    value = "canNotBeEnabled",
    disabledReason = "Disabled for CI/CD as requires secrets data: apiKey, appId"
)
@KestraTest
class IndexTest {
    @Inject
    private RunContextFactory runContextFactory;

    private RunContext getRunContext() {
        return runContextFactory.of(Map.of(
            "execution", ImmutableMap.of("id", "#12345", "flowId", "algolia-index-test", "namespace", "company.team"),
            "duration", Duration.ofMillis(123456)
        ));
    }

    @Test
    void indexRecord() throws Exception {
        RunContext runContext = getRunContext();
        String indexName = "test_products";

        Index task = Index.builder()
            .applicationId(Property.ofValue(getAppId()))
            .apiKey(Property.ofValue(getApiKey()))
            .indexName(Property.ofValue(indexName))
            .objects(Property.ofValue(List.of(Map.of(
                "objectID", "index_001",
                "name", "Index T-shirt"
            ))))
            .build();

        Index.Output output = task.run(runContext);

        assertThat(output.getResult(), notNullValue());
        assertThat(output.getResult(), hasKey("objectIDs"));

        List<String> objectIds = (List<String>) output.getResult().get("objectIDs");
        assertThat(objectIds, hasItem("index_001"));
    }

    protected static boolean canNotBeEnabled() {
        return Strings.isNullOrEmpty(getApiKey()) || Strings.isNullOrEmpty(getAppId());
    }

    protected static String getApiKey() {
        return System.getenv("ALGOLIA_API_KEY");
    }

    protected static String getAppId() {
        return System.getenv("ALGOLIA_APP_ID");
    }
}
