# opentsdb


### opentsdb写入端配置
参考[TsdbSinkDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/TsdbSinkDescriptor.java)

```yaml
url: http://localhost:4242 
metrics:    # 指定metric是哪些字段
    - score
tags:  # 指定tag是哪些字段
    - first
    - last

```

