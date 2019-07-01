package com.dfire.platform.alchemy.util;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.utils.TypeStringUtils;

public class TypeUtils {


    public static TypeInformation readTypeInfo(String typeString){

        return TypeStringUtils.readTypeInfo(typeString);
    }
}
