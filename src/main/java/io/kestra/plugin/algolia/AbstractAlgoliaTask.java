package io.kestra.plugin.algolia;

import com.algolia.api.SearchClient;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
public abstract class AbstractAlgoliaTask<T extends io.kestra.core.models.tasks.Output> extends Task implements RunnableTask<T> {
    @Schema(
        title =  "Algolia Application ID."
    )
    @NotNull
    protected Property<String> applicationId;

    @Schema(
        title = "API Key",
        description = "Algolia Admin API Key."
    )
    @NotNull
    protected Property<String> apiKey;

    protected SearchClient client(RunContext runContext) throws Exception {
        String rApplicationId = runContext.render(applicationId).as(String.class).orElseThrow();
        String rApiKeyValue = runContext.render(apiKey).as(String.class).orElseThrow();
        return new SearchClient(rApplicationId, rApiKeyValue);
    }
}
