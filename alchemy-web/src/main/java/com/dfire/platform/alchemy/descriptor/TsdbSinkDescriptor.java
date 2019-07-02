package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.connectors.tsdb.TsdbProperties;
import com.dfire.platform.alchemy.connectors.tsdb.TsdbTableSink;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author congbai
 * @date 03/06/2018
 */
public class TsdbSinkDescriptor extends SinkDescriptor {

    private String url;

    private List<String> metrics;

    private List<String> tags;

    private Integer ioThreadCount;

    private Integer batchPutBufferSize;

    private Integer batchPutConsumerThreadCount;

    private Integer batchPutSize;

    private Integer batchPutTimeLimit;

    private Integer putRequestLimit;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getIoThreadCount() {
        return ioThreadCount;
    }

    public void setIoThreadCount(Integer ioThreadCount) {
        this.ioThreadCount = ioThreadCount;
    }

    public Integer getBatchPutBufferSize() {
        return batchPutBufferSize;
    }

    public void setBatchPutBufferSize(Integer batchPutBufferSize) {
        this.batchPutBufferSize = batchPutBufferSize;
    }

    public Integer getBatchPutConsumerThreadCount() {
        return batchPutConsumerThreadCount;
    }

    public void setBatchPutConsumerThreadCount(Integer batchPutConsumerThreadCount) {
        this.batchPutConsumerThreadCount = batchPutConsumerThreadCount;
    }

    public Integer getBatchPutSize() {
        return batchPutSize;
    }

    public void setBatchPutSize(Integer batchPutSize) {
        this.batchPutSize = batchPutSize;
    }

    public Integer getBatchPutTimeLimit() {
        return batchPutTimeLimit;
    }

    public void setBatchPutTimeLimit(Integer batchPutTimeLimit) {
        this.batchPutTimeLimit = batchPutTimeLimit;
    }

    public Integer getPutRequestLimit() {
        return putRequestLimit;
    }

    public void setPutRequestLimit(Integer putRequestLimit) {
        this.putRequestLimit = putRequestLimit;
    }

    @Override
    public <T> T transform() throws Exception {
        TsdbProperties tsdbProperties = new TsdbProperties();
        BeanUtils.copyProperties(this, tsdbProperties);
        return (T)new TsdbTableSink(tsdbProperties);
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(url, "tsdb的Url不能为空");
        Assert.notNull(metrics, "tsdb的metrics不能为空");
        Assert.notNull(tags, "tsdb的tags不能为空");
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_OPENTSDB;
    }
}
