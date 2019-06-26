package com.dfire.platform.alchemy.connectors.redis;

import java.io.Serializable;

public class Sentinel implements Serializable {

    private static final long serialVersionUID = -4669551617144070538L;

    private String sentinels;

    private String master;

    public String getSentinels() {
        return sentinels;
    }

    public void setSentinels(String sentinels) {
        this.sentinels = sentinels;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }
}