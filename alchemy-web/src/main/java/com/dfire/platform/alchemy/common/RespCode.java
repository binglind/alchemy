package com.dfire.platform.alchemy.common;

/**
 * User: congbai Email: congbai@2dfire.com Date: 2016/6/13 0013 Time: 下午 7:17 Desc: 返回状态信息对象
 */
public class RespCode {

    private String code;
    private String msg;

    public RespCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
