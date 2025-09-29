package io.kestra.plugin.algolia;

import com.algolia.api.SearchClient;
import com.algolia.model.search.SearchParams;
import com.algolia.model.search.SearchResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    title = "Search Algolia Records",
    description = "Perform a search query on an Algolia index and return matching records."
)
@Plugin(
    examples = {
        @Example(
            full = true,
            title = "Search products",
            code = """
                id: search_products
                namespace: company.team

                tasks:
                  - id: search_products
                    type: io.kestra.plugin.algolia.record.Search
                    applicationId: "{{ secret('ALGOLIA_APP_ID') }}"
                    apiKey: "{{ secret('ALGOLIA_API_KEY') }}"
                    indexName: "products"
                    params:
                      query: "t-shirt"
                      hitsPerPage: 5
                """
        )
    }
)
public class Search extends AbstractAlgoliaTask<Search.Output> implements RunnableTask<Search.Output> {
    @Schema(
        title = "Index name",
        description = "The Algolia index to search."
    )
    @NotNull
    private Property<String> indexName;

    @Schema(
        title = "Search parameters",
        description = "Any valid Algolia search parameters (e.g., query, hitsPerPage, filters, facets). " +
            "Defaults to an empty object, which returns all records."
    )
    private Property<Map<String, Object>> params;

    @Override
    public Output run(RunContext runContext) throws Exception {
        try (SearchClient client = this.client(runContext)) {
            String rIndex = runContext.render(indexName).as(String.class).orElseThrow();

            Map<String, Object> params = runContext.render(this.params).asMap(String.class, Object.class);

            SearchParams searchParams = JacksonMapper.ofJson().convertValue(params, SearchParams.class);

            SearchResponse<ObjectNode> response = client.searchSingleIndex(rIndex, searchParams, ObjectNode.class);

            runContext.logger().info("Searched Algolia index '{}' with params {}, found {} hits", rIndex, params, response.getNbHits());

            return Output.builder()
                .nbHits(response.getNbHits())
                .hits(response.getHits())
                .build();
        }
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(title = "Number of hits")
        private final Integer nbHits;

        @Schema(title = "Search results (hits)")
        private final List<ObjectNode> hits;
    }
}