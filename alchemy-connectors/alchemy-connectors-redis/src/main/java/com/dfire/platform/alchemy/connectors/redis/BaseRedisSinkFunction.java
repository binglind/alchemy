package com.dfire.platform.alchemy.connectors.redis;

import com.dfire.platform.alchemy.api.common.RedisCommand;
import com.dfire.platform.alchemy.connectors.common.MetricFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.*;

/**
 * @author congbai
 * @date 07/06/2018
 */
public abstract class BaseRedisSinkFunction extends RichSinkFunction<Tuple2<Boolean, Row>> implements MetricFunction {

    private static final long serialVersionUID = 1L;

    private static final String REDIS_METRICS_GROUP = "Redis";

    private final String[] fieldNames;

    private final TypeInformation<?>[] fieldTypes;

    private final RedisProperties redisProperties;

    private final Map<String, Integer> fieldIndexs;

    private Jedis jedis;

    private Counter numRecordsOut;

    public BaseRedisSinkFunction(String[] fieldNames, TypeInformation<?>[] fieldTypes,
                                 RedisProperties redisProperties) {
        check(redisProperties);
        this.fieldNames = fieldNames;
        this.fieldTypes = fieldTypes;
        this.redisProperties = redisProperties;
        this.fieldIndexs = initFieldIndexs();
    }

    private void check(RedisProperties redisProperties) {
        Preconditions.checkNotNull(redisProperties.getName(), "redis name must not be null.");
        Preconditions.checkNotNull(redisProperties.getKeys(), "redis keys must not be null.");
        Preconditions.checkNotNull(redisProperties.getCommand(), "redis command must not be null.");
        if (redisProperties.getMembers() != null) {
            Preconditions.checkNotNull(redisProperties.getScores(), "redis score fields  must not be null.");
            Preconditions.checkArgument(redisProperties.getMembers().size() == redisProperties.getScores().size(),
                    "redis members'length must be equal to scores's length");
        }
    }

    private HashMap<String, Integer> initFieldIndexs() {
        HashMap<String, Integer> fieldIndexs = new HashMap<>(this.fieldNames.length);
        for (int i = 0; i < fieldNames.length; i++) {
            fieldIndexs.put(fieldNames[i], i);
        }
        return fieldIndexs;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.jedis = create(redisProperties);
        super.open(parameters);
    }

    protected abstract Jedis create(RedisProperties redisProperties);

    protected abstract void shutdown();

    @Override
    public void invoke(Tuple2<Boolean, Row> value, Context context) throws Exception {
        if (value == null || value.f1 == null) {
            return;
        }
        if (!value.f0) {
            return;
        }
        Row row = value.f1;
        List<String> keyValues = new LinkedList<>();
        keyValues.add(this.redisProperties.getName());
        for (String primaryKey : redisProperties.getKeys()) {
            StringBuilder primaryKV = new StringBuilder();
            int index = fieldIndexs.get(primaryKey).intValue();
            primaryKV.append(primaryKey).append(":").append(row.getField(index));
            keyValues.add(primaryKV.toString());
        }
        String keyPrefix = String.join(":", keyValues);
        RedisCommand command = RedisCommand.valueOf(this.redisProperties.getCommand().toUpperCase());
        switch (command) {
            case SET:
                sendSetCommand(keyPrefix, row);
                break;
            case HSET:
                sendHSetCommand(keyPrefix, row);
                break;
            case LPUSH:
                sendLpushCommand(keyPrefix, row);
                break;
            case RPUSH:
                sendRpushCommand(keyPrefix, row);
                break;
            case SADD:
                sendSaddCommand(keyPrefix, row);
                break;
            case ZADD:
                sendZaddCommand(keyPrefix, row);
                break;
            default:
                throw new UnsupportedOperationException("don't support redis command:" + command);
        }
        numRecordsOut = createOrGet(numRecordsOut, getRuntimeContext());
        numRecordsOut.inc();
    }

    private void sendZaddCommand(String key, Row row) {
        Map<String, Double> scores = new HashMap<>(row.getArity());
        List<String> members = this.redisProperties.getMembers();
        for (int i = 0; i < members.size(); i++) {
            int memberIndex = fieldIndexs.get(members.get(i));
            int scoreIndex = fieldIndexs.get(this.redisProperties.getScores().get(i));
            scores.put(row.getField(memberIndex).toString(), Double.valueOf(row.getField(scoreIndex).toString()));
        }
        jedis.zadd(key, scores);
        expire(key);
    }

    private void sendSaddCommand(String key, Row row) {
        List<String> members = valueList(row);
        jedis.sadd(key, members.toArray(new String[members.size()]));
        expire(key);
    }

    private List<String> valueList(Row row) {
        List<String> members = new ArrayList<>(row.getArity());
        for (int i = 0; i < fieldNames.length; i++) {
            if (containKey(fieldNames[i])) {
                continue;
            }
            members.add(row.getField(i).toString());
        }
        return members;
    }

    private void sendLpushCommand(String key, Row row) {
        List<String> values = valueList(row);
        jedis.lpush(key, values.toArray(new String[values.size()]));
        expire(key);
    }

    private void sendRpushCommand(String key, Row row) {
        List<String> values = valueList(row);
        jedis.rpush(key, values.toArray(new String[values.size()]));
        expire(key);
    }

    private void sendHSetCommand(String key, Row row) {
        Map<String, String> hash = new HashMap<>(row.getArity());
        for (int i = 0; i < fieldNames.length; i++) {
            if (containKey(fieldNames[i])) {
                continue;
            }
            hash.put(fieldNames[i], row.getField(i).toString());
        }
        jedis.hmset(key, hash);
        expire(key);
    }

    private void sendSetCommand(String keyPrefix, Row row) {
        List<String> keyValues = new ArrayList<>(row.getArity() * 2);
        List<String> keys = new ArrayList<>(row.getArity());
        for (int i = 0; i < fieldNames.length; i++) {
            if (containKey(fieldNames[i])) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(keyPrefix).append(":").append(fieldNames[i]);
            String key = builder.toString();
            keys.add(key);
            keyValues.add(key);
            keyValues.add(row.getField(i).toString());
        }
        if (keyValues.size() == 2) {
            jedis.set(keyValues.get(0), keyValues.get(1));
            expire(keys);
        } else if (keyValues.size() > 2) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.mset(keyValues.toArray(new String[keyValues.size()]));
            expire(pipeline, keys);
            pipeline.sync();
        }
    }

    private void expire(String key) {
        if (this.redisProperties.getTtl() == null) {
            return;
        }
        jedis.expire(key, this.redisProperties.getTtl());
    }

    private void expire(List<String> keys) {
        expire(null, keys);
    }

    private void expire(Pipeline pipeline, List<String> keys) {
        if (this.redisProperties.getTtl() == null) {
            return;
        }
        if (keys == null || keys.size() == 0) {
            return;
        }
        int ttl = this.redisProperties.getTtl();
        if (pipeline == null) {
            for (String key : keys) {
                jedis.expire(key, ttl);
            }
        } else {
            for (String key : keys) {
                pipeline.expire(key, ttl);
            }
        }
    }

    private boolean containKey(String key) {
        List<String> keys = this.redisProperties.getKeys();
        for (String defineKey : keys) {
            if (defineKey.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        shutdown();
        super.close();
    }

    @Override
    public String metricGroupName() {
        return REDIS_METRICS_GROUP;
    }
}
