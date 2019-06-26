package com.dfire.platform.alchemy.connectors.filesystem;

import com.dfire.platform.alchemy.connectors.common.MetricFunction;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;

public class FileSink extends BucketingSink implements MetricFunction {

    private static final String FILE_METRICS_GROUP = "File";

    private Counter numRecordsOut;

    public FileSink(String basePath) {
        super(basePath);
    }

    @Override
    public void invoke(Object value) throws Exception {
        super.invoke(value);
        numRecordsOut = createOrGet(numRecordsOut, getRuntimeContext());
        numRecordsOut.inc();
    }

    @Override
    public String metricGroupName() {
        return FILE_METRICS_GROUP;
    }
}
