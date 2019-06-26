package com.dfire.platform.alchemy.formats.hessian;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

import com.caucho.hessian.io.HessianInput;
import com.dfire.platform.alchemy.api.util.ConvertRowUtil;

/**
 * @author congbai
 * @date 2018/8/7
 */
public class HessianRowDeserializationSchema implements DeserializationSchema<Row> {

    private final TypeInformation<Row> typeInfo;

    private final Class<?> clazz;

    public HessianRowDeserializationSchema(TypeInformation<Row> typeInfo, Class<?> clazz) {
        this.typeInfo = typeInfo;
        this.clazz = clazz;
    }

    @Override
    public Row deserialize(byte[] bytes) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(is);
        Object object = hi.readObject();
        if (object.getClass() != clazz) {
            throw new ClassCastException("expected class is:" + clazz + " but is :" + object.getClass());
        }
        return ConvertRowUtil.convertToRow(object, ((RowTypeInfo)typeInfo).getFieldNames());
    }

    @Override
    public boolean isEndOfStream(Row nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Row> getProducedType() {
        return this.typeInfo;
    }
}
