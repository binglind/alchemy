[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](/LICENSE)

### Alchemy:
 > alchemy是以[Jhipster](https://www.jhipster.tech/)为脚手架开发的web系统，能够在界面上开发基于flink的sql任务，也支持拉取jar包上传任务。
 >
 > **大家如果觉得有帮助的话，请帮忙点个star**

#### 环境
- Java 8
- Maven3.x
- Flink 1.8.0  （table和hadoop依赖放入lib目录下）
- node.js (可选，开发前端需要)

#### 快速运行
```
  mvn clean package -DskipTests -Pdev,npm
```
```
  java -jar alchemy-web/target/alchemy-web-1.0.0-SNAPSHOT.jar
```
```
  访问 http://localhost:8080
  登录用户admin  密码admin
```

#### [User Guide](/docs/user_guide/business.md)

#### 支持

##### yaml配置
所有的yaml配置以"-"作为分隔符

##### flink集群模式
- 目前仅支持[standalone](/docs/static_files/cluster.md)模式

##### 源表
- [kafka010](/docs/static_files/kafka.md)
- [csv](/docs/static_files/csv.md)

##### 维表
- [mysql](/docs/static_files/mysql.md)

##### [视图](/docs/user_guide/source.md)

##### [用户函数](/docs/user_guide/udf.md)
- 页面编写用户函数
- jar包加载用户函数

##### 写入端
- [kafka010](/docs/static_files/kafka.md)
- [elasticsearch5](/docs/static_files/elasticsearch.md)
- [hbase](/docs/static_files/hbase.md)
- [redis](/docs/static_files/redis.md)
- [dubbo](/docs/static_files/dubbo.md)
- [opentsdb](/docs/static_files/opentsdb.md)
- [mysql](/docs/static_files/mysql.md)
- [file](/docs/static_files/file.md)

##### 钉钉机器人告警
resource的config目录下，修改dingtalk的webhook

![](/docs/media/15614539363428/15615363294915.jpg)


#### 生产环境
必须要做以下事情：

* dev默认是h2数据库，所以必须修改数据库配置
* 代码deploy到自己的maven私服
* 配置私服地址
 ![](docs/media/15614539363428/15615363880645.jpg)


#### 开发


#### 贡献
欢迎大家提交自己的代码


#### 近期计划

* 维表支持redis、hbase等
* 支持yarn模式
* 动态拉取schema字段
* ......

#### 联系我

  *  微信: The_quiet_night
  *  钉钉群号：23127379
