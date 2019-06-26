package com.dfire.platform.alchemy.connectors.kafka;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.streaming.connectors.kafka.KafkaTableSource;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import java.util.Properties;

/**
 * @author congbai
 * @date 2018/8/17
 */
public class AlchemyKafkaTableSource extends KafkaTableSource{

    private final DeserializationSchema<Row> deserializationSchema;


    /**
     * @param topic      Kafka topic to consume.
     * @param properties Properties for the Kafka consumer.
     * @param schema     Schema of the produced table.
     * @param returnType Type information of the produced physical DataStream.
     * @param deserializationSchema
     */
    protected AlchemyKafkaTableSource(String topic, Properties properties, TableSchema schema, TypeInformation<Row> returnType, DeserializationSchema<Row> deserializationSchema) {
        super(topic, properties, schema, returnType);
        this.deserializationSchema = deserializationSchema;
    }

    @Override
    protected FlinkKafkaConsumerBase<Row> createKafkaConsumer(String topic, Properties properties, DeserializationSchema<Row> deserializationSchema) {
         return new FlinkKafkaConsumer010<>(topic, deserializationSchema, properties);
    }

    @Override
    protected DeserializationSchema<Row> getDeserializationSchema() {
        return this.deserializationSchema;
    }

    @Override
    public String explainSource() {
        return "AlchemyKafkaTableSource";
    }

    public static class Builder extends KafkaTableSource.Builder<AlchemyKafkaTableSource,Builder>{

        private  DeserializationSchema<Row> deserializationSchema;

        private TypeInformation<Row> returnType;

        @Override
        protected boolean supportsKafkaTimestamps() {
            return true;
        }

        public Builder withDeserializationSchema(DeserializationSchema<Row> deserializationSchema) {
            this.deserializationSchema = deserializationSchema;
            return builder();
        }

        public Builder withReturnType(TypeInformation<Row> returnType) {
            this.returnType = returnType;
            return builder();
        }

        @Override
        protected Builder builder() {
            return this;
        }

        @Override
        public KafkaTableSource build() {
            Preconditions.checkNotNull(this.returnType, "returnType must not be null.");
            Preconditions.checkNotNull(this.deserializationSchema, "deserializationSchema must not be null.");
            AlchemyKafkaTableSource tableSource = new AlchemyKafkaTableSource(
                    getTopic(),
                    getKafkaProps(),
                    getTableSchema(),
                    this.returnType,
                    this.deserializationSchema);
            super.configureTableSource(tableSource);
            return tableSource;
        }
    }


}
