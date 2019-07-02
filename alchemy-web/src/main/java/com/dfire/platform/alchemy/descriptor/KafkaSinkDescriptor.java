package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.util.PropertiesUtil;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.formats.json.JsonRowSerializationSchema;
import org.apache.flink.streaming.connectors.kafka.Kafka010TableSink;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.calcite.FlinkTypeFactory;
import org.apache.flink.types.Row;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;

/**
 * @author congbai
 * @date 06/06/2018
 */
public class KafkaSinkDescriptor extends SinkDescriptor {

    private String topic;

    private Map<String, Object> properties;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public <T> T transform(TableSchema param) throws Exception {
        TableSchema tableSchema = createTableSchema();
        if (tableSchema == null) {
            tableSchema = param;
        }
        if (tableSchema == null) {
            throw new IllegalArgumentException("TableSchema must be not null");
        }
        TypeInformation[] fieldTypes = new TypeInformation[tableSchema.getFieldCount()];
        for (int i = 0; i < tableSchema.getFieldCount(); i++) {
            if (FlinkTypeFactory.isTimeIndicatorType(tableSchema.getFieldTypes()[i])) {
                fieldTypes[i] = Types.SQL_TIMESTAMP();
            }else{
                fieldTypes[i] = tableSchema.getFieldTypes()[i];
            }
        }
        TypeInformation typeInformation = new RowTypeInfo(fieldTypes, tableSchema.getFieldNames());
        SerializationSchema<Row> rowSerializationSchema = createSerializationSchema(typeInformation);
        return (T) new Kafka010TableSink(
            new TableSchema(tableSchema.getFieldNames(), fieldTypes),
            this.topic,
            PropertiesUtil.fromYamlMap(this.getProperties()),
            Optional.empty(),
            rowSerializationSchema == null ? new JsonRowSerializationSchema(typeInformation) : rowSerializationSchema
        );
    }

    @Override
    public <T> T transform() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(topic, "topic不能为空");
        Assert.notNull(properties, "properties不能为空");
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_KAFKA;
    }
}
