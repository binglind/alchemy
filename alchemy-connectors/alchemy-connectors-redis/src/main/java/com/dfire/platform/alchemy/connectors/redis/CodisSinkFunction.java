package com.dfire.platform.alchemy.connectors.redis;

import io.codis.jodis.RoundRobinJedisPool;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

/**
 * @author congbai
 * @date 2019/5/28
 */
public class CodisSinkFunction extends BaseRedisSinkFunction {

    private static final long serialVersionUID = 1L;

    private RoundRobinJedisPool pool;

    public CodisSinkFunction(String[] fieldNames, TypeInformation<?>[] fieldTypes, RedisProperties redisProperties) {
        super(fieldNames, fieldTypes, redisProperties);
    }

    @Override
    protected Jedis create(RedisProperties redisProperties) {
        Codis codis = redisProperties.getCodis();
        this.pool = RoundRobinJedisPool.create().curatorClient(codis.getZkAddrs(), codis.getZkSessionTimeoutMs())
                .zkProxyDir("/jodis/" + codis.getCodisProxyName()).poolConfig(redisProperties.getConfig())
                .database(redisProperties.getDatabase()).password(redisProperties.getPassword())
                .timeoutMs(Protocol.DEFAULT_TIMEOUT).build();
        return this.pool.getResource();
    }

    @Override
    protected void shutdown() {
        if (this.pool != null) {
            this.pool.close();
        }
    }
}
