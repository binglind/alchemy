package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.common.Field;
import com.dfire.platform.alchemy.common.TimeAttribute;
import com.dfire.platform.alchemy.util.PropertiesUtil;
import com.dfire.platform.alchemy.util.TypeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.connectors.kafka.Kafka010TableSource;
import org.apache.flink.streaming.connectors.kafka.config.StartupMode;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.KafkaValidator;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * todo 事件事件、处理时间 --> 水位
 *
 * @author congbai
 * @date 03/06/2018
 */
public class KafkaConnectorDescriptor implements ConnectorDescriptor {

    private String topic;

    private String startupMode;

    private Map<String, String> specificOffsets;

    private Map<String, Object> properties;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStartupMode() {
        return startupMode;
    }

    public void setStartupMode(String startupMode) {
        this.startupMode = startupMode;
    }

    public Map<String, String> getSpecificOffsets() {
        return specificOffsets;
    }

    public void setSpecificOffsets(Map<String, String> specificOffsets) {
        this.specificOffsets = specificOffsets;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(topic, "kafka的topic不能为空");
        Assert.notNull(properties, "kafka的properties不能为空");
        Assert.notNull(PropertiesUtil.fromYamlMap(this.properties).get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG),
            "kafak的" + ProducerConfig.BOOTSTRAP_SERVERS_CONFIG + "不能为空");

    }

    @Override
    public String type() {
        return Constants.CONNECTOR_TYPE_VALUE_KAFKA;
    }

    @Override
    public <T> T buildSource(List<Field> schema, FormatDescriptor format) throws Exception {
        return buildKafkaFlinkSource(schema, format);
    }

    private <T> T buildKafkaFlinkSource(List<Field> schema, FormatDescriptor format) throws Exception {
        String[] columnNames = new String[schema.size()];
        TypeInformation[] columnTypes = new TypeInformation[schema.size()];

        RowtimeAttributeDescriptor rowtimeAttributeDescriptor = null;
        String proctimeAttribute = null;
        for (int i = 0; i < schema.size(); i++) {
            columnNames[i] = schema.get(i).getName();
            TypeInformation typeInformation = TypeUtils.readTypeInfo(schema.get(i).getType());
            if (typeInformation == null) {
                throw new UnsupportedOperationException("Unsupported type:" + schema.get(i).getType());
            }
            columnTypes[i] = typeInformation;
            if (schema.get(i).isProctime()) {
                proctimeAttribute = schema.get(i).getName();
            } else {
                TimeAttribute timeAttribute = schema.get(i).getRowtime();
                if (timeAttribute == null) {
                    continue;
                }
                if(timeAttribute.getWatermarks() == null || timeAttribute.getTimestamps() == null){
                    throw new IllegalArgumentException("rowTime's timestamps and watermarks must be not null");
                }
                rowtimeAttributeDescriptor = new RowtimeAttributeDescriptor(schema.get(i).getName(), schema.get(i).getRowtime().getTimestamps().get(), schema.get(i).getRowtime().getWatermarks().get());
            }
        }
        StartupMode startupMode =null;
        Map<KafkaTopicPartition, Long> specificStartupOffsets = null;
        if (StringUtils.isNotEmpty(this.startupMode)) {
            switch (this.startupMode) {
                case KafkaValidator.CONNECTOR_STARTUP_MODE_VALUE_EARLIEST:
                    startupMode = StartupMode.EARLIEST;
                    break;

                case KafkaValidator.CONNECTOR_STARTUP_MODE_VALUE_LATEST:
                    startupMode = StartupMode.LATEST;
                    break;

                case KafkaValidator.CONNECTOR_STARTUP_MODE_VALUE_GROUP_OFFSETS:
                    startupMode = StartupMode.GROUP_OFFSETS;
                    break;

                case KafkaValidator.CONNECTOR_STARTUP_MODE_VALUE_SPECIFIC_OFFSETS:
                    specificStartupOffsets = new HashMap<>();
                    for (Map.Entry<String, String> entry : this.specificOffsets.entrySet()) {
                        final KafkaTopicPartition topicPartition
                            = new KafkaTopicPartition(topic, Integer.parseInt(entry.getKey()));
                        specificStartupOffsets.put(topicPartition, Long.parseLong(entry.getValue()));
                    }
                    startupMode = StartupMode.SPECIFIC_OFFSETS;
                    break;
                default:
                    startupMode = StartupMode.GROUP_OFFSETS;
            }
        }
        DeserializationSchema<Row> deserializationSchema = format.transform(new Tuple2<>(new RowTypeInfo(columnTypes, columnNames), true));
        return (T)new Kafka010TableSource(
            new TableSchema(columnNames, columnTypes),
            proctimeAttribute == null ? Optional.empty(): Optional.of(proctimeAttribute),
            rowtimeAttributeDescriptor == null ? Collections.emptyList(): Collections.singletonList(rowtimeAttributeDescriptor),
            Optional.empty(),
            this.topic,
            PropertiesUtil.fromYamlMap(this.properties),
            deserializationSchema,
            startupMode == null ? StartupMode.GROUP_OFFSETS : startupMode,
            specificStartupOffsets == null ?Collections.emptyMap() : specificStartupOffsets
        );
    }
}
