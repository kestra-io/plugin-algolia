package io.kestra.plugin.algolia;

import com.algolia.api.SearchClient;
import com.algolia.model.search.BatchRequest;
import com.algolia.model.search.BatchResponse;
import com.algolia.model.search.BatchWriteParams;
import com.algolia.model.search.Action;
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
    title = "Delete Algolia record(s).",
    description = "Delete one or more records from an Algolia index by objectID."
)
@Plugin(
    examples = {
        @Example(
            full = true,
            title = "Delete multiple product records",
            code = """
                id: delete_products
                namespace: company.team

                tasks:
                  - id: remove_products
                    type: io.kestra.plugin.algolia.record.Delete
                    applicationId: "{{ secret('ALGOLIA_APP_ID') }}"
                    apiKey: "{{ secret('ALGOLIA_API_KEY') }}"
                    indexName: "products"
                    objectIDs:
                      - "id_1"
                      - "id_2"
                """
        )
    }
)
public class Delete extends AbstractAlgoliaTask<Delete.Output> implements RunnableTask<Delete.Output> {
    @Schema(
        title = "Index name",
        description = "The Algolia index from which records will be deleted"
    )
    @NotNull
    private Property<String> indexName;

    @Schema(
        title = "Object IDs",
        description = "List of record objectIDs to delete"
    )
    @NotNull
    private Property<List<String>> objectIds;

    @Override
    public Output run(RunContext runContext) throws Exception {
        try (SearchClient client = this.client(runContext)) {
            String rIndex = runContext.render(indexName).as(String.class).orElseThrow();
            List<String> rObjectIds = runContext.render(objectIds).asList(String.class);

            List<BatchRequest> requests = rObjectIds.stream()
                .map(id -> new BatchRequest().setAction(Action.DELETE_OBJECT).setBody(Map.of("objectID", id)))
                .toList();

            BatchWriteParams params = new BatchWriteParams().setRequests(requests);
            BatchResponse response = client.batch(rIndex, params);

            runContext.logger().info("Deleted {} record(s) from index '{}'", rObjectIds.size(), rIndex);

            return Output.builder()
                .objectIds(response.getObjectIDs())
                .build();
        }
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(
            title = "Object IDs",
            description = "The requested objectIDs for deletion"
        )
        private final List<String> objectIds;
    }
}
