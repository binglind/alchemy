package com.dfire.platform.alchemy.client;

import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author congbai
 * @date 01/06/2018
 */
public class StandaloneClusterInfo {

    /**
     * 高可用模式
     */
    private String mode = HighAvailabilityMode.NONE.toString().toLowerCase();

    private String lookupTimeout;

    /**
     * high-availability.cluster-id
     */
    private String clusterId;

    /**
     * high-availability.zookeeper.quorum
     */
    private String zookeeperQuorum;

    /**
     * high-availability.storageDir
     */
    private String storagePath;

    /**
     * jobManager地址
     */
    private String address;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 集群的外部依赖
     */
    private List<String> dependencies;

    /**
     * jobManager的web url
     */
    private String webInterfaceUrl;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLookupTimeout() {
        return lookupTimeout;
    }

    public void setLookupTimeout(String lookupTimeout) {
        this.lookupTimeout = lookupTimeout;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public void setZookeeperQuorum(String zookeeperQuorum) {
        this.zookeeperQuorum = zookeeperQuorum;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public String getWebInterfaceUrl() {
        return webInterfaceUrl;
    }

    public void setWebInterfaceUrl(String webInterfaceUrl) {
        this.webInterfaceUrl = webInterfaceUrl;
    }
}
