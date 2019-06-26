package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.connectors.dubbo.DubboTableSink;
import com.dfire.platform.alchemy.connectors.elasticsearch.ElasticsearchTableSink;
import com.dfire.platform.alchemy.connectors.hbase.HbaseTableSink;
import com.dfire.platform.alchemy.connectors.redis.RedisTableSink;
import com.dfire.platform.alchemy.connectors.tsdb.TsdbTableSink;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.apache.flink.api.java.io.jdbc.JDBCAppendTableSink;
import org.apache.flink.streaming.connectors.kafka.Kafka010JsonTableSink;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class SinkDescriptorTest {

    @Test
    public void buildKafkaSink() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/kafka-sink.yaml");
        KafkaSinkDescriptor sinkDescriptor = BindPropertiesUtil.bindProperties(file, KafkaSinkDescriptor.class);
        assertThat(sinkDescriptor.getTopic()).isEqualTo("stream_dest");
        assertThat(sinkDescriptor.getProperties().get("bootstrap.servers")).isEqualTo("localhost:9092");
        Kafka010JsonTableSink tableSink = sinkDescriptor.transform();
        assertThat(tableSink).isNotNull();
    }

    @Test
    public void buildRedisSink() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/redis-sink.yaml");
        RedisSinkDescriptor sinkDescriptor = BindPropertiesUtil.bindProperties(file, RedisSinkDescriptor.class);
        assertThat(sinkDescriptor.getCommand()).isEqualTo("set");
        assertThat(sinkDescriptor.getDatabase()).isEqualTo(2);
        assertThat(sinkDescriptor.getHost()).isEqualTo("localhost");
        assertThat(sinkDescriptor.getPort()).isEqualTo(6379);
        assertThat(sinkDescriptor.getTtl()).isEqualTo(180);
        assertThat(sinkDescriptor.getKeys().get(0)).isEqualTo("first");
        RedisTableSink tableSink = sinkDescriptor.transform();
        assertThat(tableSink).isNotNull();
    }

    @Test
    public void buildMysqlSink() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/mysql-insert-sink.yaml");
        MysqlSinkDescriptor sinkDescriptor = BindPropertiesUtil.bindProperties(file, MysqlSinkDescriptor.class);
        assertThat(sinkDescriptor.getDriverName()).isNotNull();
        assertThat(sinkDescriptor.getUrl()).isNotNull();
        assertThat(sinkDescriptor.getUsername()).isEqualTo("root");
        assertThat(sinkDescriptor.getPassword()).isEqualTo("123456");
        assertThat(sinkDescriptor.getParameterTypes().length).isEqualTo(4);
        JDBCAppendTableSink tableSink = sinkDescriptor.transform();
        assertThat(tableSink).isNotNull();
    }


    @Test
    public void buildHbaseSink() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/hbase-sink.yaml");
        HbaseSinkDescriptor sinkDescriptor = BindPropertiesUtil.bindProperties(file, HbaseSinkDescriptor.class);
        assertThat(sinkDescriptor.getRowKeys().size()).isEqualTo(1);
        assertThat(sinkDescriptor.getBufferSize()).isEqualTo(1048576);
        assertThat(sinkDescriptor.getFamily()).isEqualTo("s");
        assertThat(sinkDescriptor.getFamilyColumns().get("s1").size()).isEqualTo(2);
        assertThat(sinkDescriptor.getFamilyColumns().get("s2").size()).isEqualTo(2);
        assertThat(sinkDescriptor.getNode()).isEqualTo("/hbase-unsecure");
        assertThat(sinkDescriptor.getTableName()).isEqualTo("flink-test");
        assertThat(sinkDescriptor.getZookeeper()).isEqualTo("localhost:2181");
        HbaseTableSink tableSink = sinkDescriptor.transform();
        assertThat(tableSink).isNotNull();
    }

    @Test
    public void buildTsdbSink() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/tsdb-sink.yaml");
        TsdbSinkDescriptor sinkDescriptor = BindPropertiesUtil.bindProperties(file, TsdbSinkDescriptor.class);
        assertThat(sinkDescriptor.getMetrics()).isNotNull();
        assertThat(sinkDescriptor.getTags()).isNotNull();
        assertThat(sinkDescriptor.getUrl()).isNotNull();
        TsdbTableSink tableSink = sinkDescriptor.transform();
        assertThat(tableSink).isNotNull();
    }

    @Test
    public void buildElasticsearchSink() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/es-sink.yaml");
        EsSinkDescriptor sinkDescriptor = BindPropertiesUtil.bindProperties(file, EsSinkDescriptor.class);
        assertThat(sinkDescriptor.getIndex()).isNotNull();
        assertThat(sinkDescriptor.getClusterName()).isNotNull();
        assertThat(sinkDescriptor.getTransports()).isNotNull();
        assertThat(sinkDescriptor.getIndexField()).isNotNull();
        assertThat(sinkDescriptor.getFailureHandler()).isNotNull();
        assertThat(sinkDescriptor.getConfig()).isNotNull();
        ElasticsearchTableSink tableSink = sinkDescriptor.transform();
        assertThat(tableSink).isNotNull();
    }

    @Test
    public void buildDubboSink() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/dubbo-sink.yaml");
        DubboSinkDescriptor sinkDescriptor = BindPropertiesUtil.bindProperties(file, DubboSinkDescriptor.class);
        assertThat(sinkDescriptor.getApplicationName()).isNotNull();
        assertThat(sinkDescriptor.getInterfaceName()).isNotNull();
        assertThat(sinkDescriptor.getMethodName()).isNotNull();
        assertThat(sinkDescriptor.getRegistryAddr()).isNotNull();
        DubboTableSink tableSink = sinkDescriptor.transform();
        assertThat(tableSink).isNotNull();
    }
}
