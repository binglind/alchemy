package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.connectors.elasticsearch.Elasticsearch6Properties;
import com.dfire.platform.alchemy.connectors.elasticsearch.Elasticsearch6TableSink;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.TableSchema;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public class Es6SinkDescriptor extends SinkDescriptor {

    private List<String> hosts;

    private String index;

    private String documentType;

    /**
     * 指定索引在row中的的字段，动态从row中获取索引
     */
    private String indexField;

    private boolean appendOnly;

    private String keyDelimiter;

    private String keyNullLiteral;

    private String failureHandler;

    private Boolean disableFlushOnCheckpoint;

    private Long maxRetryTimeoutMills;

    private String pathPrefix;

    private Map<String, String> config;

    public Long getMaxRetryTimeoutMills() {
        return maxRetryTimeoutMills;
    }

    public void setMaxRetryTimeoutMills(Long maxRetryTimeoutMills) {
        this.maxRetryTimeoutMills = maxRetryTimeoutMills;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
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

    public boolean isAppendOnly() {
        return appendOnly;
    }

    public void setAppendOnly(boolean appendOnly) {
        this.appendOnly = appendOnly;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getKeyDelimiter() {
        return keyDelimiter;
    }

    public void setKeyDelimiter(String keyDelimiter) {
        this.keyDelimiter = keyDelimiter;
    }

    public String getKeyNullLiteral() {
        return keyNullLiteral;
    }

    public void setKeyNullLiteral(String keyNullLiteral) {
        this.keyNullLiteral = keyNullLiteral;
    }


    public Boolean getDisableFlushOnCheckpoint() {
        return disableFlushOnCheckpoint;
    }

    public void setDisableFlushOnCheckpoint(Boolean disableFlushOnCheckpoint) {
        this.disableFlushOnCheckpoint = disableFlushOnCheckpoint;
    }

    public String getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(String failureHandler) {
        this.failureHandler = failureHandler;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
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
        Elasticsearch6Properties properties = new Elasticsearch6Properties();
        BeanUtils.copyProperties(this, properties);
        properties.setTableSchema(tableSchema);
        return (T) new Elasticsearch6TableSink(properties);
    }


    @Override
    public <T> T transform() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(this.getHosts(), "地址不能为空");
        Assert.isTrue(StringUtils.isBlank(this.getIndex()) && StringUtils.isBlank(this.getIndexField()), "索引不能为空");
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_ES6;
    }
}
