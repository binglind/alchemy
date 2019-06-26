package com.dfire.platform.alchemy.util;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.table.typeutils.TypeStringUtils;

public class TypeUtils {

    /**
     *  E.g  MAP(java.lang.String,java.lang.String)
     */
    private static final String MAP = "MAP";

    /**
     *  E.g  LIST(java.lang.String)
     */
    private static final String LIST = "LIST";

    /**
     *  E.g  ENUM(com.dfire.platform.SinkType)
     */
    private static final String ENUM = "ENUM";

    public static TypeInformation readTypeInfo(String typeString){
        if(typeString.startsWith(MAP)){
            return readMapTypeInfo(typeString);
        }else if(typeString.startsWith(LIST)){
            return readListTypeInfo(typeString);
        }else if(typeString.startsWith(ENUM)){
            return readEnumTypeInfo(typeString);
        }
        return TypeStringUtils.readTypeInfo(typeString);
    }

    private static TypeInformation readEnumTypeInfo(String typeString) {
        String[] keyValueTypes = findClassString(typeString, ENUM);
        if(keyValueTypes.length != 1){
            throw new RuntimeException("Invalid type :" + typeString);
        }
        try {
            Class clazz = ClassUtil.forName(keyValueTypes[0]);
            return Types.ENUM(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid type:" + typeString);
        }
    }

    private static TypeInformation readListTypeInfo(String typeString) {
        String[] keyValueTypes = findClassString(typeString, LIST);
        if(keyValueTypes.length != 1){
            throw new RuntimeException("Invalid type :" + typeString);
        }
        try {
            Class clazz = ClassUtil.forName(keyValueTypes[0]);
            return Types.LIST(TypeInformation.of(clazz));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid type:" + typeString);
        }
    }

    private static TypeInformation readMapTypeInfo(String typeString) {
        String[] keyValueTypes = findClassString(typeString, MAP);
        if(keyValueTypes.length != 2){
            throw new RuntimeException("Invalid type :" + typeString);
        }
        try {
            Class key = ClassUtil.forName(keyValueTypes[0]);
            Class value = ClassUtil.forName(keyValueTypes[0]);
            return Types.MAP(TypeInformation.of(key), TypeInformation.of(value));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid type:" + typeString);
        }
    }

    private static String[] findClassString(String typString, String start){
        typString = typString.trim();
        start = start.trim();
        String includeClass = typString.substring(start.length()+1, typString.length()-1);
        return includeClass.split(",");
    }
}
