package com.dfire.platform.alchemy.connectors.common;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MeterView;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.runtime.metrics.MetricNames;

public interface MetricFunction{

    default Counter createOrGet(Counter numRecordsOut, RuntimeContext runtimeContext) {
        if (numRecordsOut == null) {
            MetricGroup metricGroup = runtimeContext.getMetricGroup().addGroup(metricGroupName());
            numRecordsOut = metricGroup.counter(MetricNames.IO_NUM_RECORDS_OUT);
            metricGroup.meter(MetricNames.IO_NUM_RECORDS_OUT_RATE, new MeterView(numRecordsOut, 60));
        }
        return numRecordsOut;
    }

    String metricGroupName();
}
