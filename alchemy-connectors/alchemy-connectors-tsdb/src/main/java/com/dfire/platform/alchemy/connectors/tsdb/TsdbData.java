package com.dfire.platform.alchemy.connectors.tsdb;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * @author congbai
 * @date 2018/7/10
 */
public class TsdbData {

    private Map<String,Number> metricValues;

    private Map<String,String> tags;

    private Long timestamp;

    public TsdbData(Map<String, Number> metricValues, Map<String, String> tags, Long timestamp) {
        this.metricValues = metricValues;
        this.tags = tags;
        this.timestamp = timestamp;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public Map<String, Number> getMetricValues() {
        return metricValues;
    }

    public void setMetricValues(Map<String, Number> metricValues) {
        this.metricValues = metricValues;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public static class Builder{

        private Map<String,Number> metricValues=new HashedMap();

        private Map<String,String> tags=new HashedMap();

        private Long timestamp;

        public Builder tag(String key,String value){
            this.tags.put(key,value);
            return this;
        }

        public Builder tag(Object key,Object value){
            this.tags.put(String.valueOf(key),String.valueOf(value));
            return this;
        }


        public Builder tags(Map<String,String> tags){
            this.tags.putAll(tags);
            return this;
        }

        public Builder metricValues(Map<String,Number> metricValues){
            this.metricValues.putAll(metricValues);
            return this;
        }

        public Builder metricValue(String metric,Number value){
            this.metricValues.put(metric,value);
            return this;
        }

        public Builder timestamp(Long timestamp){
            this.timestamp=timestamp;
            return this;
        }


        public TsdbData build(){
            return new TsdbData(this.metricValues, this.tags, this.timestamp);
        }

    }

}
