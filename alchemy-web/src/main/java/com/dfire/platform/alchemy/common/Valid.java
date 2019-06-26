package com.dfire.platform.alchemy.common;

/**
 * @author congbai
 * @date 2018/6/8
 */
public enum Valid {

    DEL(0), VALID(1);
    private int valid;

    Valid(int valid) {
        this.valid = valid;
    }

    public int getValid() {
        return valid;
    }
}
