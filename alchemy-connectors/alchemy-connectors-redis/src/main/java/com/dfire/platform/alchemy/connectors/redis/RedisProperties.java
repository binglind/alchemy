package com.dfire.platform.alchemy.connectors.redis;

import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;
import java.util.List;

/**
 * @author congbai
 * @date 2018/7/12
 */
public class RedisProperties implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private JedisPoolConfig config = new JedisPoolConfig();

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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
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

    public JedisPoolConfig getConfig() {
        return config;
    }

    public void setConfig(JedisPoolConfig config) {
        this.config = config;
    }
}
