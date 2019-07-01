package com.dfire.platform.alchemy.client;

import java.util.List;
import java.util.Map;

/**
 * @author congbai
 * @date 01/06/2018
 */
public class StandaloneClusterInfo {

    /**
     * jobManager地址
     */
    private String address;

    /**
     * 端口
     */
    private Integer port;

    private Map<String, Object> properties;

    /**
     * 集群的外部依赖
     */
    private List<String> dependencies;

    /**
     * jobManager的web url
     */
    private String webInterfaceUrl;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
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
