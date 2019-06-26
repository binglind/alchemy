package com.dfire.platform.alchemy.connectors.hbase;

import com.dfire.platform.alchemy.connectors.common.MetricFunction;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.api.functions.sink.OutputFormatSinkFunction;

public class HbaseOutFormatSinkFunction extends OutputFormatSinkFunction implements MetricFunction {

    private static final String HBASE_METRICS_GROUP = "Hbase";

    private static final long serialVersionUID = 1L;

    private Counter numRecordsOut;

    public HbaseOutFormatSinkFunction(OutputFormat format) {
        super(format);
    }

    @Override
    public void invoke(Object record) throws Exception {
        super.invoke(record);
        numRecordsOut = createOrGet(numRecordsOut, getRuntimeContext());
        numRecordsOut.inc();
    }

    @Override
    public String metricGroupName() {
        return HBASE_METRICS_GROUP;
    }
}
