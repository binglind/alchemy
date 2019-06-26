package com.dfire.platform.alchemy.client.request;

import com.dfire.platform.alchemy.descriptor.SinkDescriptor;
import com.dfire.platform.alchemy.descriptor.SourceDescriptor;
import com.dfire.platform.alchemy.descriptor.UdfDescriptor;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author congbai
 * @date 04/06/2018
 */
public class SqlSubmitFlinkRequest extends AbstractSubmitRequest {

    public static final String CONFIG_KEY_RESTART_ATTEMPTS = "restartAttempts";
    public static final String CONFIG_KEY_DELAY_BETWEEN_ATTEMPTS = "delayBetweenAttempts";
    public static final String CONFIG_KEY_FAILURE_RATE = "failureRate";
    public static final String CONFIG_KEY_FAILURE_INTERVAL = "failureInterval";
    public static final String CONFIG_KEY_DELAY_INTERVAL = "delayInterval";

    private static int DEFAULT_PARALLELISM = 1;
    public List<SourceDescriptor> sources;
    public List<UdfDescriptor> udfs;
    public List<SinkDescriptor> sinks;
    private List<String> dependencies;
    private int parallelism = DEFAULT_PARALLELISM;
    private Integer maxParallelism;
    private String timeCharacteristic;
    private Long bufferTimeout;
    private String restartStrategies;
    private Map<String, Object> restartParams;
    private CheckpointConfig checkpointCfg;
    private List<String> sqls;

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public Integer getMaxParallelism() {
        return maxParallelism;
    }

    public void setMaxParallelism(Integer maxParallelism) {
        this.maxParallelism = maxParallelism;
    }

    public String getTimeCharacteristic() {
        return timeCharacteristic;
    }

    public void setTimeCharacteristic(String timeCharacteristic) {
        this.timeCharacteristic = timeCharacteristic;
    }

    public Long getBufferTimeout() {
        return bufferTimeout;
    }

    public void setBufferTimeout(Long bufferTimeout) {
        this.bufferTimeout = bufferTimeout;
    }

    public String getRestartStrategies() {
        return restartStrategies;
    }

    public void setRestartStrategies(String restartStrategies) {
        this.restartStrategies = restartStrategies;
    }

    public Map<String, Object> getRestartParams() {
        return restartParams;
    }

    public void setRestartParams(Map<String, Object> restartParams) {
        this.restartParams = restartParams;
    }

    public CheckpointConfig getCheckpointCfg() {
        return checkpointCfg;
    }

    public void setCheckpointCfg(CheckpointConfig checkpointCfg) {
        this.checkpointCfg = checkpointCfg;
    }

    public List<String> getSqls() {
        return sqls;
    }

    public void setSqls(List<String> sqls) {
        this.sqls = sqls;
    }

    public List<SourceDescriptor> getSources() {
        return sources;
    }

    public void setSources(List<SourceDescriptor> sources) {
        this.sources = sources;
    }

    public List<UdfDescriptor> getUdfs() {
        return udfs;
    }

    public void setUdfs(List<UdfDescriptor> udfs) {
        this.udfs = udfs;
    }

    public List<SinkDescriptor> getSinks() {
        return sinks;
    }

    public void setSinks(List<SinkDescriptor> sinks) {
        this.sinks = sinks;
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(parallelism, "并发数不能为空");
    }
}
