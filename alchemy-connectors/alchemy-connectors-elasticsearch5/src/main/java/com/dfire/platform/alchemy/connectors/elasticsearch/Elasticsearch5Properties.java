package com.dfire.platform.alchemy.connectors.elasticsearch;

import java.io.Serializable;
import java.util.Map;

/**
 * @author congbai
 * @date 2019/6/2
 */
public class Elasticsearch5Properties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transports;

    private String clusterName;

    private String indexType;

    private String index;

    private String indexField;

    private String failureHandler;

    private Map<String, Object> config;

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

    public String getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(String failureHandler) {
        this.failureHandler = failureHandler;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }
}
