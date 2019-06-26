package com.dfire.platform.alchemy.formats.protostuff;

import com.dfire.platform.alchemy.api.util.ConvertRowUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

import java.io.IOException;

/**
 * @author congbai
 * @date 2018/8/7
 */
public class ProtostuffRowDeserializationSchema implements DeserializationSchema<Row> {

    private final TypeInformation<Row> typeInfo;

    private final Class clazz;

    private transient Schema schema;

    public ProtostuffRowDeserializationSchema(TypeInformation<Row> typeInfo,Class clazz) {
        this.typeInfo = typeInfo;
        this.clazz = clazz;
    }

    @Override
    public Row deserialize(byte[] bytes) throws IOException {
        try {
            if(schema == null){
                this.schema = RuntimeSchema.getSchema(clazz);
            }
            Object obj = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
            // convert to row
            return ConvertRowUtil.convertToRow(obj, ((RowTypeInfo)typeInfo).getFieldNames());
        } catch (Exception e) {
           throw e;
        }
    }

    @Override
    public boolean isEndOfStream(Row row) {
        return false;
    }

    @Override
    public TypeInformation<Row> getProducedType() {
        return this.typeInfo;
    }
}
