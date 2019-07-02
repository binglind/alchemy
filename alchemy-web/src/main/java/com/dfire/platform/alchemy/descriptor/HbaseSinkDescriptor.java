package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.connectors.hbase.HbaseProperties;
import com.dfire.platform.alchemy.connectors.hbase.HbaseTableSink;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author congbai
 * @date 03/06/2018
 */
public class HbaseSinkDescriptor extends SinkDescriptor {

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

    @Override
    public <T> T transform() throws Exception {
        HbaseProperties hbaseProperties = new HbaseProperties();
        BeanUtils.copyProperties(this, hbaseProperties);
        return (T)new HbaseTableSink(hbaseProperties);
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(zookeeper, "hbase的zookeeper地址不能为空");
        Assert.notNull(node, "hbase在zookeeper的根目录不能为空");
        Assert.notNull(tableName, "hbase的表名不能为空");
        Assert.notNull(rowKeys, "hbase的rowKeys不能为空");
        Assert.isTrue(family != null || familyColumns != null, "hbase的family不能为空");
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_HBASE;
    }
}
