package com.dfire.platform.alchemy.common;

import org.apache.flink.table.sources.tsextractors.ExistingField;
import org.apache.flink.table.sources.tsextractors.StreamRecordTimestamp;
import org.apache.flink.table.sources.tsextractors.TimestampExtractor;

/**
 * @author congbai
 * @date 2018/6/30
 */
public class Timestamps {

    private String type;

    private String from;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public TimestampExtractor get(){
        if(type == null){
            return null;
        }
        if(type.equals(Type.FIELD.getType())){
            return new ExistingField(from);
        }else if(type.equals(Type.SOURCE.getType())){
            return new StreamRecordTimestamp();
        }
        return null;
    }

    public enum Type {

        FIELD("from-field"), SOURCE("from-source");

        private String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
