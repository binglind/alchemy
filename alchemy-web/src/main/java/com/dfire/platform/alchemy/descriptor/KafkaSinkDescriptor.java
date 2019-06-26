package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.util.PropertiesUtil;
import org.apache.flink.streaming.connectors.kafka.Kafka010JsonTableSink;
import org.springframework.util.Assert;

import java.util.Map;

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
    public <T> T transform() throws Exception {
        return (T)new Kafka010JsonTableSink(this.topic, PropertiesUtil.fromYamlMap(this.getProperties()));
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
