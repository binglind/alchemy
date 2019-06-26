package com.dfire.platform.alchemy.common;

/**
 * @author congbai
 * @date 2018/6/30
 */
public enum StartupMode {

    EARLIEST("earliest-offset");
    private String mode;

    StartupMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
