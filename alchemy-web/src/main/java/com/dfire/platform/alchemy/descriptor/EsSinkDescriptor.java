package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.connectors.elasticsearch.ElasticsearchProperties;
import com.dfire.platform.alchemy.connectors.elasticsearch.ElasticsearchTableSink;
import org.apache.flink.table.shaded.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author congbai
 * @date 03/06/2018
 */
public class EsSinkDescriptor extends SinkDescriptor {

    private String name;

    private String transports;

    private String clusterName;

    /**
     * 指定索引名称
     */
    private String index;

    private String indexType;

    /**
     * 指定索引在row中的的字段，动态从row中获取索引
     */
    private String indexField;

    private String failureHandler;

    private Map<String, Object> config;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransports() {
        return transports;
    }

    public void setTransports(String transports) {
        this.transports = transports;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndexField() {
        return indexField;
    }

    public void setIndexField(String indexField) {
        this.indexField = indexField;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public String getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(String failureHandler) {
        this.failureHandler = failureHandler;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    @Override
    public <T> T transform() throws Exception {
        ElasticsearchProperties properties = new ElasticsearchProperties();
        BeanUtils.copyProperties(this, properties);
        return (T)new ElasticsearchTableSink(properties);
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(transports, "地址不能为空");
        Assert.notNull(clusterName, "clusterName不能为空");
        Assert.isTrue(StringUtils.isBlank(index) && StringUtils.isBlank(indexField), "索引不能为空");
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_ES;
    }

}
