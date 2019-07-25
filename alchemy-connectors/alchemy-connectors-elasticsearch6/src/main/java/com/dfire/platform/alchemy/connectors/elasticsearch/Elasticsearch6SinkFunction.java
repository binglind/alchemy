package com.dfire.platform.alchemy.connectors.elasticsearch;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchUpsertTableSinkBase;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author dongbinglin
 */
public class Elasticsearch6SinkFunction implements ElasticsearchSinkFunction<Tuple2<Boolean, Row>> {

    private final String index;
    private final Integer fieldIndex;
    private final String docType;
    private final String keyDelimiter;
    private final String keyNullLiteral;
    private final SerializationSchema<Row> serializationSchema;
    private final XContentType contentType;
    private final ElasticsearchUpsertTableSinkBase.RequestFactory requestFactory;
    private final int[] keyFieldIndices;

    public Elasticsearch6SinkFunction(String index, Integer fieldIndex, String docType, String keyDelimiter, String keyNullLiteral, SerializationSchema<Row> serializationSchema, XContentType contentType, ElasticsearchUpsertTableSinkBase.RequestFactory requestFactory, int[] keyFieldIndices) {
        this.index = Preconditions.checkNotNull(index);
        this.fieldIndex = fieldIndex;
        this.docType = Preconditions.checkNotNull(docType);
        this.keyDelimiter = Preconditions.checkNotNull(keyDelimiter);
        this.serializationSchema = Preconditions.checkNotNull(serializationSchema);
        this.contentType = Preconditions.checkNotNull(contentType);
        this.keyFieldIndices = Preconditions.checkNotNull(keyFieldIndices);
        this.requestFactory = Preconditions.checkNotNull(requestFactory);
        this.keyNullLiteral = Preconditions.checkNotNull(keyNullLiteral);
    }


    @Override
    public void process(Tuple2<Boolean, Row> element, RuntimeContext ctx, RequestIndexer indexer) {
        if (element.f0) {
            processUpsert(element.f1, indexer);
        } else {
            processDelete(element.f1, indexer);
        }
    }

    private void processUpsert(Row row, RequestIndexer indexer) {
        final byte[] document = serializationSchema.serialize(row);
        if (keyFieldIndices.length == 0) {
            final IndexRequest indexRequest = requestFactory.createIndexRequest(
                    getIndex(row),
                    docType,
                    contentType,
                    document);
            indexer.add(indexRequest);
        } else {
            final String key = createKey(row);
            final UpdateRequest updateRequest = requestFactory.createUpdateRequest(
                    getIndex(row),
                    docType,
                    key,
                    contentType,
                    document);
            indexer.add(updateRequest);
        }
    }

    private void processDelete(Row row, RequestIndexer indexer) {
        final String key = createKey(row);
        final DeleteRequest deleteRequest = requestFactory.createDeleteRequest(
                getIndex(row),
                docType,
                key);
        indexer.add(deleteRequest);
    }

    private String createKey(Row row) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keyFieldIndices.length; i++) {
            final int keyFieldIndex = keyFieldIndices[i];
            if (i > 0) {
                builder.append(keyDelimiter);
            }
            final Object value = row.getField(keyFieldIndex);
            if (value == null) {
                builder.append(keyNullLiteral);
            } else {
                builder.append(value.toString());
            }
        }
        return builder.toString();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Elasticsearch6SinkFunction that = (Elasticsearch6SinkFunction) o;
        return Objects.equals(index, that.index) &&
                Objects.equals(fieldIndex, that.fieldIndex) &&
                Objects.equals(docType, that.docType) &&
                Objects.equals(keyDelimiter, that.keyDelimiter) &&
                Objects.equals(keyNullLiteral, that.keyNullLiteral) &&
                Objects.equals(serializationSchema, that.serializationSchema) &&
                contentType == that.contentType &&
                Objects.equals(requestFactory, that.requestFactory) &&
                Arrays.equals(keyFieldIndices, that.keyFieldIndices);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(
                index,
                fieldIndex,
                docType,
                keyDelimiter,
                keyNullLiteral,
                serializationSchema,
                contentType,
                requestFactory);
        result = 31 * result + Arrays.hashCode(keyFieldIndices);
        return result;
    }
}
