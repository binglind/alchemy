package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.Kafka010TableSource;
import org.apache.flink.streaming.connectors.kafka.KafkaTableSourceBase;
import org.apache.flink.streaming.connectors.kafka.config.StartupMode;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.types.Row;

/**
 * for kafka 0.10
 *
 * @author congbai
 */
public class Kafka010ConnectorDescriptor extends KafkaBaseConnectorDescriptor {

    @Override
    public String type() {
        return Constants.CONNECTOR_TYPE_VALUE_KAFKA010;
    }

    @Override KafkaTableSourceBase newTableSource(TableSchema schema, Optional<String> proctimeAttribute,
        List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors, Optional<Map<String, String>> fieldMapping,
        String topic, Properties properties, DeserializationSchema<Row> deserializationSchema, StartupMode startupMode,
        Map<KafkaTopicPartition, Long> specificStartupOffsets) {
        return new Kafka010TableSource(
            schema,
            proctimeAttribute,
            rowtimeAttributeDescriptors,
            fieldMapping,
            topic,
            properties,
            deserializationSchema,
            startupMode,
            specificStartupOffsets
        );
    }
}
