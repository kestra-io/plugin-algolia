# Kestra Algolia Plugin

## What

- Provides plugin components under `io.kestra.plugin.algolia`.
- Includes classes such as `Delete`, `Search`, `Index`.

## Why

- What user problem does this solve? Teams need to integrate with Algolia Search to index, query, and delete records from orchestrated workflows instead of relying on manual console work, ad hoc scripts, or disconnected schedulers.
- Why would a team adopt this plugin in a workflow? It keeps Algolia steps in the same Kestra flow as upstream preparation, approvals, retries, notifications, and downstream systems.
- What operational/business outcome does it enable? It reduces manual handoffs and fragmented tooling while improving reliability, traceability, and delivery speed for processes that depend on Algolia.

## How

### Architecture

Single-module plugin. Source packages under `io.kestra.plugin`:

- `algolia`

Infrastructure dependencies (Docker Compose services):

- `app`

### Key Plugin Classes

- `io.kestra.plugin.algolia.Delete`
- `io.kestra.plugin.algolia.Index`
- `io.kestra.plugin.algolia.Search`

### Project Structure

```
plugin-algolia/
├── src/main/java/io/kestra/plugin/algolia/
├── src/test/java/io/kestra/plugin/algolia/
├── build.gradle
└── README.md
```

## References

- https://kestra.io/docs/plugin-developer-guide
- https://kestra.io/docs/plugin-developer-guide/contribution-guidelines
