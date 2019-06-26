package com.dfire.platform.alchemy.client.response;

/**
 * @author congbai
 * @date 2018/6/20
 */
public class StatusJobResponse extends Response {

    public StatusJobResponse(boolean success) {
        super(success);
    }

    public StatusJobResponse(String message) {
        super(message);
    }
}
