# kafka.md

> 目前使用的kafka版本是0.10

### kafka源表配置demo
参考[SourceDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/SourceDescriptor.java)、[KafkaConnectorDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/KafkaConnectorDescriptor.java) 、[FormatDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/FormatDescriptor.java)

```yaml
#配置schema
schema:
  - name: syslog_host
    type: VARCHAR
  - name: syslog_tag
    type: VARCHAR
  - name: am_datetime
    type: TIMESTAMP    
  - name: am_level
    type: VARCHAR     
  - name: am_class
    type: VARCHAR  
  - name: am_marker
    type: VARCHAR      
  - name: message
    type: VARCHAR
  - name: procTime
    type: TIMESTAMP
    proctime: true
  - name: rowTime
    type: TIMESTAMP
    rowtime:
      timestamps:
        type: "from-field"
        from: "am_datetime"
      watermarks:
        type: "periodic-bounded"
        delay: "1000"  
#指定kafka相关的配置         
connector:
  topic: app-log
  properties:
      bootstrap.servers: localhost:9092
      group.id: fileGroup
#配置序列化方式      
format:
  type: grok  #指定序列化类型，目前支持grok, json ,hessian, protostuff
  properties:
      retain: true
      field-name: message
      regular: '\[%{DATA:syslog_host}\] \[%{DATA:syslog_tag}\] %{DATA:am_datetime} %{LOGLEVEL:am_level}%{SPACE}%{DATA:am_class} (?:%{DATA:am_marker}([。|：|；])|)'

```

### kafka写入端配置demo
参考KafkaSinkDescriptor

```yaml
topic: stream_dest
properties:
    bootstrap.servers: localhost:9092

```




