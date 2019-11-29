package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import java.util.Optional;
import java.util.Properties;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.connectors.kafka.Kafka010TableSink;
import org.apache.flink.streaming.connectors.kafka.KafkaTableSinkBase;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.types.Row;

/**
 * @author congbai
 */
public class Kafka010SinkDescriptor extends KafkaBaseSinkDescriptor {

    @Override KafkaTableSinkBase newTableSink(TableSchema schema, String topic, Properties properties,
        Optional<FlinkKafkaPartitioner<Row>> partitioner, SerializationSchema<Row> serializationSchema) {
        return new Kafka010TableSink(
            schema,
            topic,
            properties,
            partitioner,
            serializationSchema
        );
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_KAFKA010;
    }
}
