package com.dfire.platform.alchemy.connectors.dubbo;

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
public class DubboTableSink implements AppendStreamTableSink<Row> {

    private final DubboProperties dubboProperties;

    private String[] fieldNames;

    private TypeInformation[] fieldTypes;

    public DubboTableSink(DubboProperties dubboProperties) {
        check(dubboProperties);
        this.dubboProperties = Preconditions.checkNotNull(dubboProperties, "dubboProperties");
    }

    private void check(DubboProperties dubboProperties) {
        Preconditions.checkNotNull(dubboProperties.getApplicationName(), "dubbo applicationName must not be null.");
        Preconditions.checkNotNull(dubboProperties.getInterfaceName(), "dubbo interfaceName must not be null.");
        Preconditions.checkNotNull(dubboProperties.getMethodName(), "dubbo methodName must not be null.");
        Preconditions.checkNotNull(dubboProperties.getRegistryAddr(), "dubbo registryAddr must not be null.");
        Preconditions.checkNotNull(dubboProperties.getVersion(), "dubbo version must not be null.");
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
        DubboTableSink copy = new DubboTableSink(this.dubboProperties);
        copy.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        copy.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        Preconditions.checkArgument(fieldNames.length == fieldTypes.length,
            "Number of provided field names and types does not match.");
        return copy;
    }

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        RichSinkFunction richSinkFunction = createDubboRich();
        dataStream.addSink(richSinkFunction);
    }

    private RichSinkFunction createDubboRich() {
        return new DubboSinkFunction(dubboProperties, fieldNames);
    }
}
