package com.dfire.platform.alchemy.connectors.redis;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author congbai
 * @date 2019/5/28
 */
public class RedisSentinelSinkFunction extends BaseRedisSinkFunction {

    private static final long serialVersionUID = 1L;

    private JedisSentinelPool pool;

    public RedisSentinelSinkFunction(String[] fieldNames, TypeInformation<?>[] fieldTypes,
                                     RedisProperties redisProperties) {
        super(fieldNames, fieldTypes, redisProperties);
    }

    @Override
    protected Jedis create(RedisProperties redisProperties) {
        Set<String> sentinelset = new HashSet<>(Arrays.asList(redisProperties.getSentinel().getSentinels().split(",")));
        this.pool
                = new JedisSentinelPool(redisProperties.getSentinel().getMaster(), sentinelset, redisProperties.getConfig(),
                redisProperties.getTimeout() == null ? Protocol.DEFAULT_TIMEOUT : redisProperties.getTimeout(),
                redisProperties.getPassword(), redisProperties.getDatabase());
        return this.pool.getResource();
    }

    @Override
    protected void shutdown() {
        if (this.pool != null) {
            this.pool.destroy();
        }
    }

}
