package com.dfire.platform.alchemy.connectors.redis;

import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;

/**
 * @author congbai
 * @date 2019/5/29
 */
public class PoolConfig extends JedisPoolConfig implements Serializable {
    private static final long serialVersionUID = 1L;
}
