# cluster
目前仅支持STANDALONE模式，

### 配置
参考[StandaloneClusterInfo](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/client/StandaloneClusterInfo.java)

```yaml
mode: zookeeper  #高可用模式
lookupTimeout: 60s
cluster-id: daily-real #high-availability.cluster-id
zookeeper-quorum: localhost:2181 #high-availability.zookeeper.quorum
storage-path:  /flink/ha/real # high-availability.storageDir
port: 6123
address: 127.0.0.1 
web-interface-url: http://flink-5-1.2dfire.net  # jobManager web url
```

