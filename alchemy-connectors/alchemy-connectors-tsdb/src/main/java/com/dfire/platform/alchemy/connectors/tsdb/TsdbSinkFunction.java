package com.dfire.platform.alchemy.connectors.tsdb;

import com.dfire.platform.alchemy.connectors.common.MetricFunction;
import com.dfire.platform.alchemy.connectors.tsdb.handler.HitsdbHandler;
import com.dfire.platform.alchemy.connectors.tsdb.handler.TsdbHandler;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author congbai
 * @date 2018/7/10
 */
public class TsdbSinkFunction extends RichSinkFunction<Row> implements MetricFunction {

    private static final long serialVersionUID = 1L;

    private static final String TSDB_METRICS_GROUP = "Tsdb";

    private final TsdbProperties tsdbProperties;

    private final String[] fieldNames;

    private final MapFunction<String, String> tagMapFunction;

    private final Map<String, Integer> fieldIndexs;

    private  Counter numRecordsOut;

    private transient TsdbHandler tsdbHandler;

    public TsdbSinkFunction(TsdbProperties tsdbProperties, String[] fieldNames, MapFunction<String, String> tagMapFunction) {
        this.tsdbProperties = tsdbProperties;
        this.fieldNames = fieldNames;
        this.tagMapFunction = tagMapFunction;
        this.fieldIndexs = initFieldIndexs();
    }
    private HashMap<String, Integer> initFieldIndexs() {
        HashMap<String, Integer> fieldIndexs = new HashMap<>(this.fieldNames.length);
        for (int i=0; i<fieldNames.length; i++){
            fieldIndexs.put(fieldNames[i], i);
        }
        return fieldIndexs;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.tsdbHandler = new HitsdbHandler(tsdbProperties);
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        this.tsdbHandler.close();
        super.close();
    }

    @Override
    public void invoke(Row value, Context context) throws Exception {
        if (value == null) {
            return;
        }
        TsdbData tsdbData = createDate(value, context);
        this.tsdbHandler.execute(tsdbData);
        numRecordsOut = createOrGet(numRecordsOut, getRuntimeContext());
        numRecordsOut.inc();
    }

    private TsdbData createDate(Row value, Context context) throws Exception {
        Map<String, Number> metrics = createMetrics(value);
        Map<String, String>  tags = createTags(value);
        Long timestamp = context.timestamp();
        if (timestamp == null){
            timestamp = context.currentWatermark() == Long.MIN_VALUE ? context.currentProcessingTime() : context.currentWatermark();
        }
        return TsdbData.newBuilder().metricValues(metrics).tags(tags).timestamp(timestamp).build();
    }

    private Map<String, String> createTags(Row input) throws Exception {
        List<String> tags = this.tsdbProperties.getTags();
        Map<String, String> returnValue = new HashMap<>(tags.size());
        for (String tag : tags){
            int index = this.fieldIndexs.get(tag);
            String tagValue = input.getField(index).toString();
            if(tagMapFunction != null){
                tagValue = tagMapFunction.map(tagValue);
            }
            if(tagValue != null){
                returnValue.put(tag.trim(), tagValue);
            }
        }
        return returnValue;
    }

    private Map<String, Number> createMetrics(Row input) {
        List<String> metrics = this.tsdbProperties.getMetrics();
        Map<String, Number> returnValue = new HashMap<>(metrics.size());
        for (String metric : metrics){
            int index = this.fieldIndexs.get(metric);
            returnValue.put(metric.trim(), (Number)input.getField(index));
        }
        return returnValue;
    }

    @Override
    public String metricGroupName() {
        return TSDB_METRICS_GROUP;
    }
}
