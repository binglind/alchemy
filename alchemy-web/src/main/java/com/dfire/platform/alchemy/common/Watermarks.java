package com.dfire.platform.alchemy.common;

import org.apache.flink.table.sources.wmstrategies.AscendingTimestamps;
import org.apache.flink.table.sources.wmstrategies.BoundedOutOfOrderTimestamps;
import org.apache.flink.table.sources.wmstrategies.PreserveWatermarks;
import org.apache.flink.table.sources.wmstrategies.WatermarkStrategy;

/**
 * @author congbai
 * @date 2018/6/30
 */
public class Watermarks {

    private String type;

    private long delay;

    public WatermarkStrategy get() {
        if (type == null) {
            return null;
        }
        if (type.equals(Type.PERIODIC_ASCENDING.getType())) {
            return new AscendingTimestamps();
        } else if (type.equals(Type.PERIODIC_BOUNDED.getType())) {
            return new BoundedOutOfOrderTimestamps(delay);
        } else if (type.equals(Type.FROM_SOURCE.getType())) {
            return new PreserveWatermarks();
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public enum Type {

        PERIODIC_BOUNDED("periodic-bounded"), PERIODIC_ASCENDING("periodic-ascending"), FROM_SOURCE("from-source");

        private String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
