# hbase
### hbase写入端配置
参考[HbaseSinkDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/FileSystemSinkDescriptor.java)

```yaml
row-keys:  #指定rowkey的列，如下组成的rowkey为id的值，多列的值以“-”分隔
    - id
buffer-size: 1048576 # byte #批量写入的大小
family: s    # 只有单个family时，指定名称
family-columns:    #多个family时，指定每个family包含的column
    s1:           # family s1包含id,first列
        - first
        - id
    s2:          # family s2包含last,score
        - last
        - score
node: /hbase-unsecure 
table-name: flink-test
zookeeper: localhost:2181

```


