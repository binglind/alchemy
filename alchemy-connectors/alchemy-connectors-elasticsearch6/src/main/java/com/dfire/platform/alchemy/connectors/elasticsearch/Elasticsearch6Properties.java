package com.dfire.platform.alchemy.connectors.elasticsearch;

import org.apache.flink.table.api.TableSchema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Elasticsearch6Properties implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> hosts;

    private String index;

    private String documentType;

    /**
     * 指定索引在row中的的字段，动态从row中获取索引
     */
    private String indexField;

    private boolean appendOnly = true;

    private String keyDelimiter;

    private String keyNullLiteral;

    private String failureHandler;

    private Boolean disableFlushOnCheckpoint;

    private Integer maxRetryTimeoutMills;

    private String pathPrefix;

    private Map<String, String> config;

    private TableSchema tableSchema;

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

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
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

    public String getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(String failureHandler) {
        this.failureHandler = failureHandler;
    }

    public Boolean getDisableFlushOnCheckpoint() {
        return disableFlushOnCheckpoint;
    }

    public void setDisableFlushOnCheckpoint(Boolean disableFlushOnCheckpoint) {
        this.disableFlushOnCheckpoint = disableFlushOnCheckpoint;
    }

    public Integer getMaxRetryTimeoutMills() {
        return maxRetryTimeoutMills;
    }

    public void setMaxRetryTimeoutMills(Integer maxRetryTimeoutMills) {
        this.maxRetryTimeoutMills = maxRetryTimeoutMills;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public TableSchema getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(TableSchema tableSchema) {
        this.tableSchema = tableSchema;
    }
}
