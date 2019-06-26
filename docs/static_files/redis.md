# redis

### redis写入端配置
参考[RedisSinkDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/RedisSinkDescriptor.java),支持Sentinel、单机、Codis三种

```yaml
#sentinel:    #sentinel配置
#    sentinels: localhost:26379
#    master: cache101
host: localhost  #单机时指定redis地址
port: 6379
database: 2
keys: #指定key的列名
    - first
command: set #支持SET,HSET,RPUSH,LPUSH,SADD,ZADD命令，详见RedisCommand
members:  #使用zadd命令时，必须指定members的列
    - first
scores: #使用zadd命令时，必须指定scores的列，与members一对一对应
    - score
ttl: 1800  # key过期时间

```


