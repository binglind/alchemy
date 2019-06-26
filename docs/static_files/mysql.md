# mysql
### mysql写入端配置
参考[MysqlConnectorDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/MysqlConnectorDescriptor.java)

```yaml
driver-name: com.zaxxer.hikari.HikariDataSource #默认driverClass，需要替换可自行加入依赖包
url: jdbc:mysql://localhost:3306/flink_test?useUnicode=true&characterEncoding=utf8&useSSL=false
username: root
password: 123456
query: replace into insert_test (id,first,last,score) values (?,?,?,?) #sql语句
parameter-types:   #指定mysql列的类型
    - 'INT'
    - 'VARCHAR'
    - 'VARCHAR'
    - 'DOUBLE'
```

### mysql维表配置
参考[SourceDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/SourceDescriptor.java)、[MysqlConnectorDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/MysqlConnectorDescriptor.java)

```yaml
schema:  #表包含的字段
    - name: id
      type: VARCHAR
    - name: name
      type: VARCHAR
side:  #维表信息，参考Side
    async: true #异步
    cache-type: LRU  #缓存类型
    cache-size: 1000 #缓存大小
    ttl: 60000 #缓存过期时间
    timeout: 10000 #异步调用超时时间
    partition: false #是否分区
connector:  #mysql配置
    url: jdbc:mysql://localhost:3306/flink_test?useUnicode=true&characterEncoding=utf8&useSSL=false
    username: root
    password: 123456

```



