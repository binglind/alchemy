# csv
参考[SourceDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/SourceDescriptor.java)、[CsvConnectorDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/CsvConnectorDescriptor.java)

```yaml
schema:
    - name: first
      type: VARCHAR
    - name: id
      type: INT
    - name: score
      type: DOUBLE
    - name: last
      type: VARCHAR
connector:
    path: ~/Code/platform/alchemy/alchemy-web/src/test/resources/source.csv
    field-delim: '#'
    row-delim: '$'
    ignore-first-line: true
    ignore-comments: '%'

```


