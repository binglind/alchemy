package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.api.common.Side;
import com.dfire.platform.alchemy.connectors.common.Cache;
import com.dfire.platform.alchemy.connectors.common.LruCache;
import com.dfire.platform.alchemy.connectors.common.side.SideTable;
import com.dfire.platform.alchemy.connectors.kafka.AlchemyKafkaTableSource;
import com.dfire.platform.alchemy.connectors.mysql.side.MysqlAsyncSideFunction;
import com.dfire.platform.alchemy.formats.grok.GrokRowDeserializationSchema;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.table.sources.CsvTableSource;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.tsextractors.ExistingField;
import org.apache.flink.table.sources.wmstrategies.BoundedOutOfOrderTimestamps;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceDescriptorTest {

    @Test
    public void buildKafkaSource() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/kafka-source.yaml");
        SourceDescriptor sourceDescriptor = BindPropertiesUtil.bindProperties(file, SourceDescriptor.class);
        KafkaConnectorDescriptor connectorDescriptor = BindPropertiesUtil.bindProperties(sourceDescriptor.getConnector(), KafkaConnectorDescriptor.class);
        assertThat(connectorDescriptor.getTopic()).isEqualTo("app-log");
        assertThat(connectorDescriptor.getStartupMode()).isEqualTo("earliest-offset");
        assertThat(connectorDescriptor.getSpecificOffsets().get("1")).isEqualTo("1000");
        assertThat(connectorDescriptor.getSpecificOffsets().get("2")).isEqualTo("3000");
        assertThat(connectorDescriptor.getProperties().get("zookeeper.connect")).isEqualTo("127.0.0.1:2181/kafka");
        assertThat(connectorDescriptor.getProperties().get("bootstrap.servers")).isEqualTo("127.0.0.1:9092");
        assertThat(connectorDescriptor.getProperties().get("group.id")).isEqualTo("testGroup");
        assertThat(sourceDescriptor.getSchema()).isNotNull();
        FormatDescriptor formatDescriptor = sourceDescriptor.getFormat();
        AlchemyKafkaTableSource alchemyKafkaTableSource = connectorDescriptor.buildSource(sourceDescriptor.getSchema(), formatDescriptor);
        assertThat(alchemyKafkaTableSource).isNotNull();
        assertThat(alchemyKafkaTableSource.getProctimeAttribute()).isEqualTo("procTime");
        List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors = alchemyKafkaTableSource.getRowtimeAttributeDescriptors();
        assertThat(rowtimeAttributeDescriptors).isNotNull();
        assertThat(rowtimeAttributeDescriptors.get(0).getAttributeName()).isEqualTo("rowTime");
        assertThat(rowtimeAttributeDescriptors.get(0).getTimestampExtractor()).isInstanceOf(ExistingField.class);
        assertThat(rowtimeAttributeDescriptors.get(0).getWatermarkStrategy()).isInstanceOf(BoundedOutOfOrderTimestamps.class);
        DeserializationSchema deserializationSchema = formatDescriptor.transform(alchemyKafkaTableSource.getReturnType());
        assertThat(deserializationSchema).isInstanceOf(GrokRowDeserializationSchema.class);
    }


    @Test
    public void buildCsvSource() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/csv-source.yaml");
        SourceDescriptor sourceDescriptor = BindPropertiesUtil.bindProperties(file, SourceDescriptor.class);
        CsvConnectorDescriptor connectorDescriptor = BindPropertiesUtil.bindProperties(sourceDescriptor.getConnector(), CsvConnectorDescriptor.class);
        assertThat(connectorDescriptor.getPath()).isNotNull();
        assertThat(connectorDescriptor.getFieldDelim()).isNotNull();
        assertThat(connectorDescriptor.getRowDelim()).isNotNull();
        assertThat(connectorDescriptor.getIgnoreComments()).isNotNull();
        assertThat(connectorDescriptor.isIgnoreFirstLine()).isTrue();
        assertThat(sourceDescriptor.getSchema()).isNotNull();
        CsvTableSource tableSource = connectorDescriptor.buildSource(sourceDescriptor.getSchema(), null);
        assertThat(tableSource).isNotNull();
    }


    @Test
    public void buildMysqlSide() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/mysql-side.yaml");
        SourceDescriptor sourceDescriptor = BindPropertiesUtil.bindProperties(file, SourceDescriptor.class);
        MysqlConnectorDescriptor connectorDescriptor = BindPropertiesUtil.bindProperties(sourceDescriptor.getConnector(), MysqlConnectorDescriptor.class);
        assertThat(connectorDescriptor.getUrl()).isNotNull();
        assertThat(connectorDescriptor.getUsername()).isNotNull();
        assertThat(connectorDescriptor.getPassword()).isNotNull();
        Side side = sourceDescriptor.getSide();
        assertThat(side).isNotNull();
        assertThat(side.isAsync()).isTrue();
        SideTable sideTable = new SideTable();
        sideTable.setSide(side);
        MysqlAsyncSideFunction function = connectorDescriptor.buildSource(sourceDescriptor.getSchema(), null, sideTable);
        assertThat(function).isNotNull();
        Cache cache =  function.create(side);
        assertThat(cache).isInstanceOf(LruCache.class);
    }


}
