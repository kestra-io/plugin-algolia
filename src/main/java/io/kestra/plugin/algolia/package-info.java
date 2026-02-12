@PluginSubGroup(
    description = "Tasks that integrate with Algolia Search to index, query, and delete records.\n" +
        "They use your Algolia Application ID and Admin API Key to operate on a target index, supporting batch writes, replacements, and removals.\n" +
        "Use them to populate indexes (Index), clean up by objectID (Delete), or retrieve hits with any Algolia search parameters (Search).",
    categories = {
        PluginSubGroup.PluginCategory.DATA,
        PluginSubGroup.PluginCategory.INFRASTRUCTURE
    }
)
package io.kestra.plugin.algolia;

import io.kestra.core.models.annotations.PluginSubGroup;
