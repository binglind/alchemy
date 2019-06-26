package com.dfire.platform.alchemy.client.response;

/**
 * @author congbai
 * @date 01/06/2018
 */
public class Response {

    protected boolean success;

    protected String message;

    public Response(boolean success) {
        this.success = success;
    }

    public Response(String message) {
        this.message = message;
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
