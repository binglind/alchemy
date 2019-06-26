# source.md
数据源包含三种类型，表、视图、维表
![](/docs/media/15615333222030/15615337816768.jpg)
1. 配置数据源名称，如果是mysql维表，必须和表名一致。sql任务中使用的是这个名称。
2. 选择类型
3. 选择数据源类型，如果是表，支持kafka和csv;如果是维表，仅支持mysql
4. 备注
5. 用yaml格式配置数据源的信息，如果是表，需要包含schema、connector、format，可以参考[kafka](/docs/static_files/kafka.md)；如果是视图，配置select语句，如select * from other_table

