package com.dfire.platform.alchemy.connectors.hbase;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.OutputFormatSinkFunction;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sinks.UpsertStreamTableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

/**
 * @author congbai
 * @date 05/06/2018
 */
public class HbaseTableSink implements UpsertStreamTableSink<Row> {

    private final HbaseProperties hbaseProperties;

    private String[] fieldNames;

    private TypeInformation[] fieldTypes;

    public HbaseTableSink(HbaseProperties hbaseProperties) {
        this.hbaseProperties = hbaseProperties;
    }

    @Override
    public String[] getFieldNames() {
        return this.fieldNames;
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return this.fieldTypes;
    }

    @Override
    public TableSink<Tuple2<Boolean, Row>> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        HbaseTableSink copy = new HbaseTableSink(this.hbaseProperties);
        copy.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        copy.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        Preconditions.checkArgument(fieldNames.length == fieldTypes.length,
            "Number of provided field names and types does not match.");
        return copy;
    }

    @Override
    public void setKeyFields(String[] keys) {

    }

    @Override
    public void setIsAppendOnly(Boolean isAppendOnly) {

    }

    @Override
    public TupleTypeInfo<Tuple2<Boolean, Row>> getOutputType() {
        return new TupleTypeInfo(Types.BOOLEAN, getRecordType());
    }

    @Override
    public TypeInformation<Row> getRecordType() {
        return new RowTypeInfo(getFieldTypes());
    }

    @Override
    public void emitDataStream(DataStream<Tuple2<Boolean, Row>> dataStream) {
        OutputFormatSinkFunction hbaseSink = creatHbaseSink();
        dataStream.addSink(hbaseSink);
    }

    private OutputFormatSinkFunction creatHbaseSink() {
        return new HbaseOutFormatSinkFunction(
            new HBaseOutputFormat(this.hbaseProperties, this.fieldNames, this.fieldTypes));
    }

}
