package com.dfire.platform.alchemy.connectors.elasticsearch;

import com.dfire.platform.alchemy.api.util.RandomUtils;
import com.dfire.platform.alchemy.connectors.common.MetricFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.formats.json.JsonRowSerializationSchema;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.types.Row;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class ElasticsearchTableFunction implements MetricFunction,  ElasticsearchSinkFunction<Row>, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchTableFunction.class);

    private static final long serialVersionUID = 1L;

    private static final String ELASTICSEARCH_METRICS_GROUP = "Elasticsearch";

    private final String index;

    private final String type;

    private final Integer fieldIndex;

    private final JsonRowSerializationSchema jsonRowSchema;

    private MapFunction<byte[], byte[]> mapFunction;

    private Counter numRecordsOut;


    public ElasticsearchTableFunction(String index,
                                      Integer fieldIndex,
                                      String type,
                                      JsonRowSerializationSchema jsonRowSchema,
                                      MapFunction<byte[], byte[]> mapFunction) {
        if(type == null){
            this.type = "*";
        }else{
            this.type = type;
        }

        this.index = index;
        this.jsonRowSchema = jsonRowSchema;
        this.fieldIndex = fieldIndex;
        this.mapFunction = mapFunction;
    }

    @Override
    public void process(Row row, RuntimeContext runtimeContext,
                        RequestIndexer requestIndexer) {
        if (row == null ) {
            return;
        }
        requestIndexer.add(createIndexRequest(row, runtimeContext));
        numRecordsOut = createOrGet(numRecordsOut, runtimeContext);
        numRecordsOut.inc();
    }

    private IndexRequest createIndexRequest(Row row, RuntimeContext  runtimeContext) {
        byte[] source = this.jsonRowSchema.serialize(row);
        if(mapFunction != null){
            try {
                source = mapFunction.map(source);
            } catch (Exception e) {
                logger.error("Map Failed", e);
            }
        }
        return Requests.indexRequest().index(getIndex(row))
                .id(RandomUtils.uuid()).type(type).source(source);
    }


    /**
     * 获取自定义索引名
     *
     * @param row
     * @return
     */
    private String getIndex(Row row) {
        if (fieldIndex == null) {
            return this.index;
        }
        return (String) row.getField(fieldIndex);
    }

    @Override
    public String metricGroupName() {
        return ELASTICSEARCH_METRICS_GROUP;
    }
}