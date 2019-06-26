package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.api.common.RedisCommand;
import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.connectors.redis.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author congbai
 * @date 03/06/2018
 */
public class RedisSinkDescriptor extends SinkDescriptor {

    private String name;

    private String host;

    private int port;

    private int database;

    private String password;

    private Integer timeout;

    private Integer ttl;

    private String command;

    private List<String> keys;

    private List<String> members;

    private List<String> scores;

    private Codis codis;

    private Sentinel sentinel;

    private PoolConfig config = new PoolConfig();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getScores() {
        return scores;
    }

    public void setScores(List<String> scores) {
        this.scores = scores;
    }

    public Codis getCodis() {
        return codis;
    }

    public void setCodis(Codis codis) {
        this.codis = codis;
    }

    public Sentinel getSentinel() {
        return sentinel;
    }

    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    public PoolConfig getConfig() {
        return config;
    }

    public void setConfig(PoolConfig config) {
        this.config = config;
    }

    @Override
    public <T> T transform() throws Exception {
        RedisProperties redisProperties = new RedisProperties();
        BeanUtils.copyProperties(this, redisProperties);
        return (T)new RedisTableSink(redisProperties);
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(database, "redis的database不能为空");
        Assert.notNull(keys, "redis的keys不能为空");
        Assert.notNull(command, "redis的command不能为空");
        Assert.notNull(RedisCommand.valueOf(command), "redis的command不能为空");
        Assert.isTrue(codis != null || sentinel != null, "必须配置codis或者sentinel参数");
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_REDIS;
    }

}
