# elasticsearch
### es写入端配置
参考[EsSinkDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/EsSinkDescriptor.java)

```yaml
cluster-name: daily #集群名
transports: localhost:9300 #es集群地址
index: flink-test #写死索引名
#index-field: first  #指定索引列
failure-handler: IGNORE #写入失败处理策略，参考FailureHandler
config:  #es sink的额外配置
    bulk.flush.max.actions: 100

```


