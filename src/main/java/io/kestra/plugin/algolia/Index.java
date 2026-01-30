package io.kestra.plugin.algolia;

import com.algolia.api.SearchClient;
import com.algolia.model.search.Action;
import com.algolia.model.search.BatchRequest;
import com.algolia.model.search.BatchResponse;
import com.algolia.model.search.BatchWriteParams;
import com.fasterxml.jackson.core.type.TypeReference;
import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.runners.RunContext;
import io.kestra.core.serializers.JacksonMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@SuperBuilder
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Schema(
    title = "Index or replace Algolia records",
    description = "Batch-add records to an Algolia index; any object with the same `objectID` is replaced. Runs a single batch write using the Admin API Key."
)
@Plugin(
    examples = {
        @Example(
            full = true,
            title = "Index a single product record",
            code = """
                id: index_product
                namespace: company.team

                tasks:
                  - id: index
                    type: io.kestra.plugin.algolia.record.Index
                    applicationId: "{{ secret('ALGOLIA_APP_ID') }}"
                    apiKey: "{{ secret('ALGOLIA_API_KEY') }}"
                    indexName: "products"
                    objects:
                      - objectID: "prod_123"
                        name: "Black T-shirt"
                        color: "black"
                """
        ),
        @Example(
            full = true,
            title = "Index multiple product records",
            code = """
                id: batch_products
                namespace: company.team

                tasks:
                  - id: add_products
                    type: io.kestra.plugin.algolia.record.Index
                    applicationId: "{{ secret('ALGOLIA_APP_ID') }}"
                    apiKey: "{{ secret('ALGOLIA_API_KEY') }}"
                    indexName: "products"
                    objects:
                      - objectID: "prod_001"
                        name: "Blue T-shirt"
                      - objectID: "prod_002"
                        name: "Running Shoes"
                """
        )
    }
)
public class Index extends AbstractAlgoliaTask<Index.Output> implements RunnableTask<Index.Output> {
    @Schema(
        title = "Target index name",
        description = "Algolia index to write into; must exist or be created beforehand in your application."
    )
    @NotNull
    private Property<String> indexName;

    @Schema(
        title = "Record objects",
        description = "List of JSON maps sent as-is to Algolia. If an item includes `objectID`, it will be replaced; otherwise Algolia generates one."
    )
    @NotNull
    private Property<List<Map<String, Object>>> objects;

    @Override
    public Output run(RunContext runContext) throws Exception {
        try (SearchClient client = this.client(runContext)) {
            String rIndexName = runContext.render(indexName).as(String.class).orElseThrow();
            List<Map<String, Object>> rObjects = runContext.render(objects).asList(Map.class);

            List<BatchRequest> batchRequests = rObjects.stream()
                .map(obj -> new BatchRequest().setAction(Action.ADD_OBJECT).setBody(obj))
                .toList();

            BatchWriteParams params = new BatchWriteParams().setRequests(batchRequests);
            BatchResponse response = client.batch(rIndexName, params);

            runContext.logger().info("Indexed {} Algolia record(s) into index '{}'", rObjects.size(), rIndexName);

            Map<String, Object> result = JacksonMapper.ofJson().convertValue(response, new TypeReference<>() {});

            return Output.builder()
                .result(result)
                .build();
        }
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(
            title = "Raw batch response",
            description = "Full response returned by Algolia batch write, including taskID and objectIDs."
        )
        private final Map<String, Object> result;
    }
}
