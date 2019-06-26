package com.dfire.platform.alchemy.connectors.redis;

import java.util.List;

public class Cluster {

    private List<String> nodes;

    private Integer maxRedirects;

    public List<String> getNodes() {
        return this.nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public Integer getMaxRedirects() {
        return this.maxRedirects;
    }

    public void setMaxRedirects(Integer maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

}
