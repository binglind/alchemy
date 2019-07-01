package com.dfire.platform.alchemy.descriptor;

/**
 * @author congbai
 * @date 01/06/2018
 */
public interface CoreDescriptor<R> extends Descriptor {

    String getName();

    <T> T transform() throws Exception;

    default <T> T transform(R param) throws Exception {
        return transform();
    }

}
