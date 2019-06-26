package com.dfire.platform.alchemy.formats.hessian;

import java.io.ByteArrayOutputStream;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

import com.caucho.hessian.io.HessianOutput;
import com.dfire.platform.alchemy.api.util.ConvertRowUtil;

/**
 * @author congbai
 * @date 2018/8/7
 */
public class HessianRowSerializationSchema implements SerializationSchema<Row> {

    private final TypeInformation<Row> typeInfo;
    private final Class<?> clazz;
    private transient ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public HessianRowSerializationSchema(TypeInformation<Row> typeInfo, Class<?> clazz) {
        this.typeInfo = typeInfo;
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(Row row) {
        outputStream.reset();
        try {
            Object object = clazz.newInstance();
            ConvertRowUtil.convertFromRow(object, ((RowTypeInfo)typeInfo).getFieldNames(), row);
            HessianOutput ho = new HessianOutput(outputStream);
            ho.writeObject(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Could not serialize row '" + row + "'. " + "Make sure that the schema matches the input.", e);
        }
    }
}
