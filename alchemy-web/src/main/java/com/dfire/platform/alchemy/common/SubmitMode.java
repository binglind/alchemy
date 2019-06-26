package com.dfire.platform.alchemy.common;

/**
 * 提交方式，jar包 或者sql语句
 *
 * @author congbai
 * @date 01/06/2018
 */
public enum SubmitMode {

    JAR(1), SQL(2);

    private int mode;

    SubmitMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
