package com.dfire.platform.alchemy.connectors.tsdb;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.sinks.AppendStreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

/**
 * @author congbai
 * @date 2018/7/10
 */
public class TsdbTableSink implements AppendStreamTableSink<Row> {

    private final TsdbProperties tsdbProperties;

    private String[] fieldNames;

    private TypeInformation[] fieldTypes;

    public TsdbTableSink(TsdbProperties tsdbProperties) {
        check(tsdbProperties);
        this.tsdbProperties = Preconditions.checkNotNull(tsdbProperties, "tsdbProperties");;
    }

    private void check(TsdbProperties tsdbProperties) {
        Preconditions.checkNotNull(tsdbProperties.getUrl(), "tsdb url must not be null.");
        Preconditions.checkNotNull(tsdbProperties.getMetrics(), "tsdb metrics must not be null.");
        Preconditions.checkNotNull(tsdbProperties.getTags(), "tsdb tags must not be null.");

    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(this.fieldTypes);
    }

    @Override
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        TsdbTableSink copy = new TsdbTableSink(this.tsdbProperties);
        copy.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        copy.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        Preconditions.checkArgument(fieldNames.length == fieldTypes.length,
            "Number of provided field names and types does not match.");
        return copy;
    }

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        RichSinkFunction richSinkFunction = createTsdbRich();
        dataStream.addSink(richSinkFunction);
    }

    private RichSinkFunction createTsdbRich() {
        MapFunction<String, String> tagMapFunction = createMapFunction(tsdbProperties.getMapClazz());
        return new TsdbSinkFunction(tsdbProperties, fieldNames, tagMapFunction);
    }

    private MapFunction<String, String> createMapFunction(String mapClazz) {
        if(mapClazz == null || mapClazz.trim().length() == 0){
            return null;
        }
        try {
            Class<MapFunction<String, String>> clazz = (Class<MapFunction<String, String>>) Class.forName(mapClazz);
            return clazz.newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
