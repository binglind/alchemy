package com.dfire.platform.alchemy.print;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.table.sinks.AppendStreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

/**
 * todo  结果实时反馈页面
 * @author congbai
 * @date 2018/9/14
 */
public class PrintTableSink implements AppendStreamTableSink<Row> {

    private String[] fieldNames;

    private TypeInformation[] fieldTypes;

    @Override
    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(getFieldTypes());
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
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        PrintTableSink copy = new PrintTableSink();
        copy.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        copy.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        Preconditions.checkArgument(fieldNames.length == fieldTypes.length,
            "Number of provided field names and types does not match.");
        return copy;
    }

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        PrintSinkFunction<Row> printSinkFunction = new PrintSinkFunction<>();
        dataStream.addSink(printSinkFunction).name("print_sink");
    }
}
