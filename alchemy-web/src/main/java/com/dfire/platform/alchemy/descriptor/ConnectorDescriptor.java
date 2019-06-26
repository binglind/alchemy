package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Field;

import java.util.List;

/**
 * @author congbai
 * @date 2018/6/30
 */
public interface ConnectorDescriptor extends Descriptor {

    <T> T buildSource(List<Field> schema, FormatDescriptor format) throws Exception;

    default <T, R> T buildSource(List<Field> schema, FormatDescriptor format, R param) throws Exception {
        return buildSource(schema, format);
    }
}
