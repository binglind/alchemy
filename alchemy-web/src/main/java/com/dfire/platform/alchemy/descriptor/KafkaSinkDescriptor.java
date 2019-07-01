package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.util.PropertiesUtil;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.formats.json.JsonRowSerializationSchema;
import org.apache.flink.streaming.connectors.kafka.Kafka010TableSink;
import org.apache.flink.table.api.TableSchema;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;

/**
 * @author congbai
 * @date 06/06/2018
 */
public class KafkaSinkDescriptor extends SinkDescriptor {

    private String name;

    private String topic;

    private Map<String, Object> properties;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
        if(param == null){
            throw new IllegalArgumentException("TableSchema must be not null");
        }
        return (T) new Kafka010TableSink(
            param,
            this.topic,
            PropertiesUtil.fromYamlMap(this.getProperties()),
            Optional.empty(),
            new JsonRowSerializationSchema(new RowTypeInfo(param.getFieldTypes(), param.getFieldNames())));
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
