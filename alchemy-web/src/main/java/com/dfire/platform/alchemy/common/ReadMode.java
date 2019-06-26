package com.dfire.platform.alchemy.common;

/**
 * 读取内容方式，code或者jar
 *
 * @author congbai
 * @date 01/06/2018
 */
public enum ReadMode {

    JAR(1), CODE(2);

    private int mode;

    ReadMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
