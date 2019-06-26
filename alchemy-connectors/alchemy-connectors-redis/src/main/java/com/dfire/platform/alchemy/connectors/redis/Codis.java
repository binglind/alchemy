package com.dfire.platform.alchemy.connectors.redis;

import java.io.Serializable;

/**
 * @author dongbinglin
 */
public class Codis implements Serializable {

    private static final long serialVersionUID = -5872593113132939832L;

    private String zkAddrs;

    /**
     * 分业务维度，由codis管理员统一分配
     */
    private String codisProxyName;

    /**
     * 如无必要不用去设置
     */
    private int zkSessionTimeoutMs = 30000;

    public String getZkAddrs() {
        return zkAddrs;
    }

    public void setZkAddrs(String zkAddrs) {
        this.zkAddrs = zkAddrs;
    }

    public String getCodisProxyName() {
        return codisProxyName;
    }

    public void setCodisProxyName(String codisProxyName) {
        this.codisProxyName = codisProxyName;
    }

    public int getZkSessionTimeoutMs() {
        return zkSessionTimeoutMs;
    }

    public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
        this.zkSessionTimeoutMs = zkSessionTimeoutMs;
    }

}