package com.dfire.platform.alchemy.connectors.filesystem;

import java.io.Serializable;

/**
 * @author congbai
 * @date 2019/5/30
 */
public class FilePropereties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String basePath;

    private String dateFormat;

    private Writer writer;

    private Long batchSize;

    private Long inactiveBucketCheckInterval;

    private Long inactiveBucketThreshold;

    private String inProgressSuffix;

    private String inProgressPrefix;

    private String pendingSuffix;

    private String pendingPrefix;

    private String validLengthSuffix;

    private String validLengthPrefix;

    private String partPrefix;

    private String partSuffix;

    private Boolean useTruncate;

    private Long asyncTimeout;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public Long getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Long batchSize) {
        this.batchSize = batchSize;
    }

    public Long getInactiveBucketCheckInterval() {
        return inactiveBucketCheckInterval;
    }

    public void setInactiveBucketCheckInterval(Long inactiveBucketCheckInterval) {
        this.inactiveBucketCheckInterval = inactiveBucketCheckInterval;
    }

    public Long getInactiveBucketThreshold() {
        return inactiveBucketThreshold;
    }

    public void setInactiveBucketThreshold(Long inactiveBucketThreshold) {
        this.inactiveBucketThreshold = inactiveBucketThreshold;
    }

    public String getInProgressSuffix() {
        return inProgressSuffix;
    }

    public void setInProgressSuffix(String inProgressSuffix) {
        this.inProgressSuffix = inProgressSuffix;
    }

    public String getInProgressPrefix() {
        return inProgressPrefix;
    }

    public void setInProgressPrefix(String inProgressPrefix) {
        this.inProgressPrefix = inProgressPrefix;
    }

    public String getPendingSuffix() {
        return pendingSuffix;
    }

    public void setPendingSuffix(String pendingSuffix) {
        this.pendingSuffix = pendingSuffix;
    }

    public String getPendingPrefix() {
        return pendingPrefix;
    }

    public void setPendingPrefix(String pendingPrefix) {
        this.pendingPrefix = pendingPrefix;
    }

    public String getValidLengthSuffix() {
        return validLengthSuffix;
    }

    public void setValidLengthSuffix(String validLengthSuffix) {
        this.validLengthSuffix = validLengthSuffix;
    }

    public String getValidLengthPrefix() {
        return validLengthPrefix;
    }

    public void setValidLengthPrefix(String validLengthPrefix) {
        this.validLengthPrefix = validLengthPrefix;
    }

    public String getPartPrefix() {
        return partPrefix;
    }

    public void setPartPrefix(String partPrefix) {
        this.partPrefix = partPrefix;
    }

    public String getPartSuffix() {
        return partSuffix;
    }

    public void setPartSuffix(String partSuffix) {
        this.partSuffix = partSuffix;
    }

    public Boolean getUseTruncate() {
        return useTruncate;
    }

    public void setUseTruncate(Boolean useTruncate) {
        this.useTruncate = useTruncate;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Long getAsyncTimeout() {
        return asyncTimeout;
    }

    public void setAsyncTimeout(Long asyncTimeout) {
        this.asyncTimeout = asyncTimeout;
    }
}
