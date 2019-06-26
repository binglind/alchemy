package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.connectors.filesystem.FilePropereties;
import com.dfire.platform.alchemy.connectors.filesystem.FileSystemTableSink;
import com.dfire.platform.alchemy.connectors.filesystem.Writer;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * @author congbai
 * @date 03/06/2018
 */
public class FileSystemSinkDescriptor extends SinkDescriptor {

    private String name;

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

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
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

    public Long getAsyncTimeout() {
        return asyncTimeout;
    }

    public void setAsyncTimeout(Long asyncTimeout) {
        this.asyncTimeout = asyncTimeout;
    }

    @Override
    public <T> T transform() throws Exception {
        FilePropereties filePropereties = new FilePropereties();
        BeanUtils.copyProperties(this, filePropereties);
        return (T)new FileSystemTableSink(filePropereties);
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(basePath, "file的basePath不能为空");
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_FILESYSTEM;
    }
}
