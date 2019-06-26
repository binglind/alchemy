package com.dfire.platform.alchemy.formats.protostuff;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

import com.dfire.platform.alchemy.api.util.ConvertRowUtil;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author congbai
 * @date 2018/8/7
 */
public class ProtostuffRowSerializationSchema implements SerializationSchema<Row> {

    private final static ThreadLocal<LinkedBuffer> THREAD_LOCAL = new ThreadLocal<LinkedBuffer>() {

        @Override
        protected LinkedBuffer initialValue() {
            return LinkedBuffer.allocate();
        }
    };
    private final TypeInformation<Row> typeInfo;

    private final Class clazz;

    private transient  Schema schema;

    public ProtostuffRowSerializationSchema(Class clazz, TypeInformation<Row> typeInfo) {
        this.clazz =clazz;
        this.typeInfo = typeInfo;
    }

    @Override
    public byte[] serialize(Row row) {
        if(this.schema == null){
            this.schema = RuntimeSchema.getSchema(clazz);
        }
        LinkedBuffer buf = THREAD_LOCAL.get();
        try {
            Object object = schema.newMessage();
            ConvertRowUtil.convertFromRow(object, ((RowTypeInfo)typeInfo).getFieldNames(), row);
            return ProtostuffIOUtil.toByteArray(object, schema, buf);
        } catch (Throwable t) {
            throw new RuntimeException(
                "Could not serialize row '" + row + "'. " + "Make sure that the schema matches the input.", t);
        } finally {
            buf.clear();
        }
    }

}
