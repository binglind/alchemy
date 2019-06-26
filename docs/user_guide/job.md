# job.md
支持sql任务和jar包任务

![](/docs/media/15615354825725/15615355167699.jpg)

1. 输入任务名称
2. 选择类型，sql或者jar
3. 输入备注
4. 选择集群
5. 用yaml格式配置任务信息


### sql配置

参考[SqlSubmitFlinkRequest](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/client/request/SqlSubmitFlinkRequest.java)

```yaml
parallelism: 1
time-characteristic: EventTime
restart-strategies:  FIXED  #重试策略 [RestartStrategies](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/common/RestartStrategies.java)
restart-params:  #重试参数
    restart-attempts: 10
    delay-between-attempts: 10000
checkpoint-config:  # checkpoint配置
    checkpoint-timeout: 900000
    checkpoint-interval: 10000
dependencies:
    - org.apache.flink:flink-json:1.5.4  # 任务依赖包


```


### jar配置

参考[JarSubmitFlinkRequest](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/client/request/JarSubmitFlinkRequest.java)

```yaml
#just for test
dependency: org.apache.flink:flink-sql-client:1.5.4  # 需要运行jar包的maven依赖
parallelism: 2 #并发
program-args: --sampler 2 --test true #额外的参数
# just for test，
entry-class: org.apache.flink.table.client.SqlClient # 执行main方法的类的全路径
savepoint-path: /flink/savepoint
allow-non-restored-state: true

```