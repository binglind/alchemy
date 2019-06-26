package com.dfire.platform.alchemy.client.response;

public class SavepointResponse extends Response{

    private String path;

    public SavepointResponse(boolean success) {
        super(success);
    }

    public SavepointResponse(boolean success, String path) {
        super(success);
        this.path = path;
    }

    public SavepointResponse(String message) {
        super(message);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
