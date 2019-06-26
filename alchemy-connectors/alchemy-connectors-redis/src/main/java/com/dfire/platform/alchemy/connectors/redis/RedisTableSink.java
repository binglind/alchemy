package com.dfire.platform.alchemy.connectors.redis;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sinks.UpsertStreamTableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author congbai
 * @date 07/06/2018
 */
public class RedisTableSink implements UpsertStreamTableSink<Row> {

    protected static final Logger LOG = LoggerFactory.getLogger(RedisTableSink.class);

    private final RedisProperties redisProperties;

    private String[] fieldNames;

    private TypeInformation[] fieldTypes;

    public RedisTableSink(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
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
    public TableSink<Tuple2<Boolean, Row>> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        RedisTableSink copy = new RedisTableSink(redisProperties);
        copy.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        copy.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        Preconditions.checkArgument(fieldNames.length == fieldTypes.length,
                "Number of provided field names and types does not match.");
        return copy;
    }

    @Override
    public void setKeyFields(String[] keys) {
        LOG.info("redis keys:{}", keys);
    }

    @Override
    public void setIsAppendOnly(Boolean isAppendOnly) {
        LOG.info("RedisTableSink is appendOnly:{}", isAppendOnly);
    }

    @Override
    public void emitDataStream(DataStream<Tuple2<Boolean, Row>> dataStream) {
        RichSinkFunction richSinkFunction = createRedisRich();
        dataStream.addSink(richSinkFunction);
    }

    private RichSinkFunction createRedisRich() {
        return RedisFactory.getInstance(this.fieldNames, this.fieldTypes, this.redisProperties);
    }

    @Override
    public TupleTypeInfo<Tuple2<Boolean, Row>> getOutputType() {
        return new TupleTypeInfo(Types.BOOLEAN, getRecordType());
    }

    @Override
    public TypeInformation<Row> getRecordType() {
        return new RowTypeInfo(getFieldTypes());
    }

}
