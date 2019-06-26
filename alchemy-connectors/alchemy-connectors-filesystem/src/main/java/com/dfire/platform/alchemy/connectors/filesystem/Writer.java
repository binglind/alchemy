package com.dfire.platform.alchemy.connectors.filesystem;

import java.io.Serializable;

public class Writer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String compressionCodecName;

    private String compressionType;

    public String getCompressionCodecName() {
        return compressionCodecName;
    }

    public void setCompressionCodecName(String compressionCodecName) {
        this.compressionCodecName = compressionCodecName;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }
}