# Kestra Algolia Plugin

## What

- Provides plugin components under `io.kestra.plugin.algolia`.
- Includes classes such as `Delete`, `Search`, `Index`.

## Why

- This plugin integrates Kestra with Algolia.
- It provides tasks that integrate with Algolia Search to index, query, and delete records.

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
