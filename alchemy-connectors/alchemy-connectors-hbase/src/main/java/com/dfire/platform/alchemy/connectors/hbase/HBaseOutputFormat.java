package com.dfire.platform.alchemy.connectors.hbase;

import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.shaded.org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author dongbinglin
 */
public class HBaseOutputFormat implements OutputFormat<Tuple2<Boolean, Row>>{

    private static final long serialVersionUID = 1L;

    private static final String HBASE_QUORUM = "hbase.zookeeper.quorum";

    private static final String HBASE_ZNODE_PARENT = "zookeeper.znode.parent";

    private final HbaseProperties hbaseProperties;

    private final String[] fieldNames;

    private final TypeInformation[] fieldTypes;

    private final Map<String, Integer> fieldIndexs;

    private final Map<String, String> columnFamily;

    private final SimpleDateFormat format;

    private transient org.apache.hadoop.conf.Configuration conf = null;

    private transient HTable table = null;

    public HBaseOutputFormat(HbaseProperties hbaseProperties, String[] fieldNames, TypeInformation[] fieldTypes) {
        check(hbaseProperties);
        this.hbaseProperties = hbaseProperties;
        this.fieldNames = fieldNames;
        this.fieldTypes = fieldTypes;
        this.fieldIndexs = initFieldIndexs();
        this.format = initFormat();
        this.columnFamily = initColumnFamily();
    }

    private void check(HbaseProperties properties) {
        Preconditions.checkNotNull(properties.getRowKeys(), "hbase rowkeys must not be null.");
        Preconditions.checkNotNull(properties.getTableName(), "hbase tableName must not be null.");
        Preconditions.checkNotNull(properties.getNode(), "hbase node must not be null.");
        Preconditions.checkNotNull(properties.getZookeeper(), "hbase zookeeper must not be null.");
        Preconditions.checkArgument((properties.getFamily() != null) || (properties.getFamilyColumns() != null),
            "hbase family must not be null");
    }

    private Map<String, String> initColumnFamily() {
        if (this.hbaseProperties.getFamilyColumns() == null) {
            return null;
        }
        Map<String, String> columnFamily = new HashMap<>();
        Map<String, List<String>> familyColumns = this.hbaseProperties.getFamilyColumns();
        for (Map.Entry<String, List<String>> familyColumn : familyColumns.entrySet()) {
            String family = familyColumn.getKey();
            List<String> columns = familyColumn.getValue();
            for (int j = 0; j < columns.size(); j++) {
                columnFamily.put(columns.get(j), family);
            }
        }
        return columnFamily;
    }

    private SimpleDateFormat initFormat() {
        if (StringUtils.isNotEmpty(this.hbaseProperties.getDateFormat())) {
            return new SimpleDateFormat(this.hbaseProperties.getDateFormat());
        }
        return null;
    }

    private HashMap<String, Integer> initFieldIndexs() {
        HashMap<String, Integer> fieldIndexs = new HashMap<>(this.fieldNames.length);
        for (int i = 0; i < fieldNames.length; i++) {
            fieldIndexs.put(fieldNames[i], i);
        }
        return fieldIndexs;
    }

    @Override
    public void configure(Configuration parameters) {
        conf = HBaseConfiguration.create();
        conf.set(HBASE_QUORUM, this.hbaseProperties.getZookeeper());
        conf.set(HBASE_ZNODE_PARENT, this.hbaseProperties.getNode());
    }

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
        table = new HTable(conf, this.hbaseProperties.getTableName());
        if (this.hbaseProperties.getBufferSize() > 0) {
            table.setAutoFlushTo(false);
            table.setWriteBufferSize(this.hbaseProperties.getBufferSize());
        }
    }

    @Override
    public void writeRecord(Tuple2<Boolean, Row> value) throws IOException {
        if (value == null || value.f1 == null) {
            return;
        }
        if (!value.f0) {
            return;
        }
        Row input = value.f1;
        String rowKey = createRowKey(input);
        Put put = new Put(Bytes.toBytes(rowKey));
        final String[] columnNames = this.fieldNames;
        for (int i = 0; i < input.getArity(); i++) {
            String family = findFamily(i);
            if (family == null) {
                continue;
            }
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(columnNames[i]),
                Bytes.toBytes(getValue(input.getField(i))));
        }
        if (this.hbaseProperties.isSkipWal()) {
            put.setDurability(Durability.SKIP_WAL);
        }
        table.put(put);
    }

    private String findFamily(int index) {
        if (this.columnFamily != null) {
            String column = this.fieldNames[index];
            return this.columnFamily.get(column);
        } else {
            return this.hbaseProperties.getFamily();
        }
    }

    private String createRowKey(Row input) {
        List<String> rowKeyList = new ArrayList<>();
        List<String> rowkeys = this.hbaseProperties.getRowKeys();
        for (int i = 0; i < rowkeys.size(); ++i) {
            String colName = rowkeys.get(i);
            int index = this.fieldIndexs.get(colName);
            Object columnValue = input.getField(index);
            rowKeyList.add(getValue(columnValue));
        }
        return StringUtils.join(rowKeyList, "-");
    }

    private String getValue(Object columnValue) {
        if (columnValue instanceof java.util.Date) {
            Date timestamp = (Date)columnValue;
            return getTimeValue(timestamp);
        } else {
            return columnValue.toString();
        }
    }

    private String getTimeValue(java.util.Date date) {
        if (format == null) {
            return String.valueOf(date.getTime());
        } else {
            return format.format(date);
        }
    }

    @Override
    public void close() throws IOException {
        table.flushCommits();
        table.close();
    }
}