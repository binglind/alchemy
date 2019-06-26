package com.dfire.platform.alchemy.common;

import java.io.Serializable;

/**
 * @author congbai
 * @date 2019/6/10
 */
public class Resource implements Serializable {

    private String queue;

    private Long memory;

    private Long vCores;

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }

    public Long getvCores() {
        return vCores;
    }

    public void setvCores(Long vCores) {
        this.vCores = vCores;
    }
}
