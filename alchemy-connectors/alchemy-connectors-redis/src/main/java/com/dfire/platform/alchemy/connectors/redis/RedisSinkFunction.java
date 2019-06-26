package com.dfire.platform.alchemy.connectors.redis;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * @author congbai
 * @date 2019/5/28
 */
public class RedisSinkFunction extends BaseRedisSinkFunction {

    private static final long serialVersionUID = 1L;

    private JedisPool pool;

    public RedisSinkFunction(String[] fieldNames, TypeInformation<?>[] fieldTypes, RedisProperties redisProperties) {
        super(fieldNames, fieldTypes, redisProperties);
    }

    @Override
    protected Jedis create(RedisProperties redisProperties) {
        this.pool = new JedisPool(
                redisProperties.getConfig(),
                redisProperties.getHost(),
                redisProperties.getPort(),
                redisProperties.getTimeout() == null ? Protocol.DEFAULT_TIMEOUT : redisProperties.getTimeout(),
                redisProperties.getPassword(),
                redisProperties.getDatabase());
        return this.pool.getResource();
    }

    @Override
    protected void shutdown() {
        if (this.pool != null) {
            this.pool.close();
        }
    }
}
