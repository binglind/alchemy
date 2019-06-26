package com.dfire.platform.alchemy.connectors.redis;

import org.apache.flink.api.common.typeinfo.TypeInformation;

/**
 * @author congbai
 * @date 2019/5/28
 */
public class RedisFactory {

    public static BaseRedisSinkFunction getInstance(String[] fieldNames, TypeInformation[] fieldTypes,
                                                    RedisProperties properties) {
        if (properties.getSentinel() != null) {
            return new RedisSentinelSinkFunction(fieldNames, fieldTypes, properties);
        }
        if (properties.getCodis() != null) {
            return new CodisSinkFunction(fieldNames, fieldTypes, properties);
        }
        return new RedisSinkFunction(fieldNames, fieldTypes, properties);
    }
}
