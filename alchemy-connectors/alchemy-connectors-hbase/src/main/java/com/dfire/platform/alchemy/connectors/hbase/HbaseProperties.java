package com.dfire.platform.alchemy.connectors.hbase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author congbai
 * @date 2018/7/12
 */
public class HbaseProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String zookeeper;

    private String node;

    private String tableName;

    private List<String> rowKeys;

    /**
     * 一行只有单个family
     */
    private String family;

    /**
     * 一行有多个family
     */
    private Map<String, List<String>> familyColumns;

    private String dateFormat;

    private long bufferSize;

    private boolean skipWal;


    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getRowKeys() {
        return rowKeys;
    }

    public void setRowKeys(List<String> rowKeys) {
        this.rowKeys = rowKeys;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Map<String, List<String>> getFamilyColumns() {
        return familyColumns;
    }

    public void setFamilyColumns(Map<String, List<String>> familyColumns) {
        this.familyColumns = familyColumns;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public long getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(long bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean isSkipWal() {
        return skipWal;
    }

    public void setSkipWal(boolean skipWal) {
        this.skipWal = skipWal;
    }
}
