# elasticsearch
### es5写入端配置
参考[Es5SinkDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/Es5SinkDescriptor.java)

```yaml
cluster-name: daily #集群名
transports: localhost:9300 #es集群地址
index: flink-test #写死索引名
#index-field: first  #指定索引列
failure-handler: IGNORE #写入失败处理策略，参考FailureHandler
config:  #es sink的额外配置
    bulk.flush.max.actions: 100

```


### es6写入端配置
参考[Es6SinkDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/Es6SinkDescriptor.java)

```yaml
hosts:
    - localhost:9200
index: flink-test
document-type: test
failure-handler: IGNORE
config:
    bulk-flush-max-actions: 3

```