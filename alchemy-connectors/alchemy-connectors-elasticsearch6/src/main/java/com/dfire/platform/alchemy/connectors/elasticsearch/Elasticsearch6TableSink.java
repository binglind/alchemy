/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dfire.platform.alchemy.connectors.elasticsearch;


import com.dfire.platform.alchemy.connectors.common.util.ActionRequestFailureHandlerUtil;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.formats.json.JsonRowSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkBase;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchUpsertTableSinkBase;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch6.RestClientFactory;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sinks.UpsertStreamTableSink;
import org.apache.flink.table.typeutils.TypeCheckUtils;
import org.apache.flink.table.utils.TableConnectorUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.common.xcontent.XContentType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 支持从row中动态获取index
 *
 * @author dongbinglin
 */
public class Elasticsearch6TableSink implements UpsertStreamTableSink<Row> {

    public static final String BULK_FLUSH_MAX_ACTIONS = "bulkFlushMaxActions";

    public static final String BULK_FLUSH_MAX_SIZE = "bulkFlushMaxSizeMb";

    public static final String BULK_FLUSH_INTERVAL = "bulkFlushInterval";

    public static final String BULK_FLUSH_BACKOFF = "bulkFlushBackoff";

    public static final String BULK_FLUSH_BACKOFF_TYPE = "bulkFlushBackoffType";

    public static final String BULK_FLUSH_BACKOFF_RETRIES = "bulkFlushBackoffRetries";

    public static final String BULK_FLUSH_BACKOFF_DELAY = "bulkFlushBackoffDelay";

    public static final String DEFAULT_KEY_DELIMITER = "_";

    public static final String DEFAULT_KEY_NULL_LITERAL = "";

    private final Elasticsearch6Properties elasticsearch6Properties;

    private final String indexField;

    /**
     * Flag that indicates that only inserts are accepted.
     */
    private final boolean isAppendOnly;

    /**
     * Schema of the table.
     */
    private final TableSchema schema;

    /**
     * Version-agnostic hosts configuration.
     */
    private final List<ElasticsearchUpsertTableSinkBase.Host> hosts;

    /**
     * Default index for all requests.
     */
    private final String index;

    /**
     * Default document type for all requests.
     */
    private final String docType;

    /**
     * Delimiter for composite keys.
     */
    private final String keyDelimiter;

    /**
     * String literal for null keys.
     */
    private final String keyNullLiteral;

    /**
     * Serialization schema used for the document.
     */
    private final SerializationSchema<Row> serializationSchema;

    /**
     * Content type describing the serialization schema.
     */
    private final XContentType contentType;

    /**
     * Failure handler for failing {@link ActionRequest}s.
     */
    private final ActionRequestFailureHandler failureHandler;


    /**
     * Version-agnostic creation of {@link ActionRequest}s.
     */
    private final ElasticsearchUpsertTableSinkBase.RequestFactory requestFactory;

    /**
     * Key field indices determined by the query.
     */
    private int[] keyFieldIndices = new int[0];


    public Elasticsearch6TableSink(Elasticsearch6Properties elasticsearch6Properties) {
        this.elasticsearch6Properties = Preconditions.checkNotNull(elasticsearch6Properties);
        this.isAppendOnly = elasticsearch6Properties.isAppendOnly();
        this.schema = Preconditions.checkNotNull(elasticsearch6Properties.getTableSchema());
        this.index = Preconditions.checkNotNull(elasticsearch6Properties.getIndex());
        this.keyDelimiter = elasticsearch6Properties.getKeyDelimiter() == null ? DEFAULT_KEY_DELIMITER : elasticsearch6Properties.getKeyDelimiter();
        this.keyNullLiteral = elasticsearch6Properties.getKeyNullLiteral() == null ?DEFAULT_KEY_NULL_LITERAL : elasticsearch6Properties.getKeyNullLiteral();
        this.docType = Preconditions.checkNotNull(elasticsearch6Properties.getDocumentType());
        this.serializationSchema = new JsonRowSerializationSchema(new RowTypeInfo(elasticsearch6Properties.getTableSchema().getFieldTypes(), elasticsearch6Properties.getTableSchema().getFieldNames()));
        this.contentType = XContentType.JSON;
        this.hosts = Preconditions.checkNotNull(createHosts(elasticsearch6Properties.getHosts()));
        this.failureHandler = Preconditions.checkNotNull(ActionRequestFailureHandlerUtil.createFailureHandler(elasticsearch6Properties.getFailureHandler()));
        this.requestFactory = new Elasticsearch6RequestFactory();
        this.indexField = elasticsearch6Properties.getIndexField();
    }

    @Override
    public void setKeyFields(String[] keyNames) {
        if (keyNames == null) {
            this.keyFieldIndices = new int[0];
            return;
        }

        final String[] fieldNames = getFieldNames();
        final int[] keyFieldIndices = new int[keyNames.length];
        for (int i = 0; i < keyNames.length; i++) {
            keyFieldIndices[i] = -1;
            for (int j = 0; j < fieldNames.length; j++) {
                if (keyNames[i].equals(fieldNames[j])) {
                    keyFieldIndices[i] = j;
                    break;
                }
            }
            if (keyFieldIndices[i] == -1) {
                throw new RuntimeException("Invalid key fields: " + Arrays.toString(keyNames));
            }
        }

        validateKeyTypes(keyFieldIndices);

        this.keyFieldIndices = keyFieldIndices;
    }

    @Override
    public TypeInformation<Tuple2<Boolean, Row>> getOutputType() {
        return Types.TUPLE(Types.BOOLEAN, getRecordType());
    }

    @Override
    public String[] getFieldNames() {
        return schema.getFieldNames();
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return schema.getFieldTypes();
    }

    @Override
    public TableSink<Tuple2<Boolean, Row>> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        if (!Arrays.equals(getFieldNames(), fieldNames) || !Arrays.equals(getFieldTypes(), fieldTypes)) {
            throw new ValidationException("Reconfiguration with different fields is not allowed. " +
                    "Expected: " + Arrays.toString(getFieldNames()) + " / " + Arrays.toString(getFieldTypes()) + ". " +
                    "But was: " + Arrays.toString(fieldNames) + " / " + Arrays.toString(fieldTypes));
        }
        return new Elasticsearch6TableSink(elasticsearch6Properties);
    }

    @Override
    public void setIsAppendOnly(Boolean isAppendOnly) {
        if (this.isAppendOnly && !isAppendOnly) {
            throw new ValidationException(
                    "The given query is not supported by this sink because the sink is configured to " +
                            "operate in append mode only. Thus, it only support insertions (no queries " +
                            "with updating results).");
        }
    }

    @Override
    public TypeInformation<Row> getRecordType() {
        return schema.toRowType();
    }

    @Override
    public void emitDataStream(DataStream<Tuple2<Boolean, Row>> dataStream) {
        Elasticsearch6SinkFunction upsertFunction = new Elasticsearch6SinkFunction(
                index,
                findIndex(indexField, schema.getFieldNames()),
                docType,
                keyDelimiter,
                keyNullLiteral,
                serializationSchema,
                contentType,
                requestFactory,
                keyFieldIndices);
        final SinkFunction<Tuple2<Boolean, Row>> sinkFunction = createSinkFunction(
                hosts,
                failureHandler,
                upsertFunction);
        dataStream.addSink(sinkFunction)
                .name(TableConnectorUtils.generateRuntimeName(this.getClass(), getFieldNames()));

    }

    private Integer findIndex(String indexField, String[] fieldNames) {
        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(indexField)) {
                return i;
            }
        }
        return null;
    }

    private SinkFunction<Tuple2<Boolean, Row>> createSinkFunction(List<ElasticsearchUpsertTableSinkBase.Host> hosts, ActionRequestFailureHandler failureHandler, Elasticsearch6SinkFunction upsertFunction) {
        final List<HttpHost> httpHosts = hosts.stream()
                .map((host) -> new HttpHost(host.hostname, host.port, host.protocol))
                .collect(Collectors.toList());

        final ElasticsearchSink.Builder<Tuple2<Boolean, Row>> builder = new ElasticsearchSink.Builder(httpHosts, upsertFunction);

        builder.setFailureHandler(failureHandler);
        final Map<String, String> bulkConfig = elasticsearch6Properties.getConfig();
        if (bulkConfig != null) {
            Optional.ofNullable(bulkConfig.get(BULK_FLUSH_MAX_ACTIONS))
                    .ifPresent(v -> builder.setBulkFlushMaxActions(Integer.valueOf(v)));

            Optional.ofNullable(bulkConfig.get(BULK_FLUSH_MAX_SIZE))
                    .ifPresent(v -> builder.setBulkFlushMaxSizeMb(MemorySize.parse(v).getMebiBytes()));

            Optional.ofNullable(bulkConfig.get(BULK_FLUSH_INTERVAL))
                    .ifPresent(v -> builder.setBulkFlushInterval(Long.valueOf(v)));

            Optional.ofNullable(bulkConfig.get(BULK_FLUSH_BACKOFF))
                    .ifPresent(v -> builder.setBulkFlushBackoff(Boolean.valueOf(v)));

            Optional.ofNullable(bulkConfig.get(BULK_FLUSH_BACKOFF_TYPE))
                    .ifPresent(v -> builder.setBulkFlushBackoffType(ElasticsearchSinkBase.FlushBackoffType.valueOf(v)));

            Optional.ofNullable(bulkConfig.get(BULK_FLUSH_BACKOFF_RETRIES))
                    .ifPresent(v -> builder.setBulkFlushBackoffRetries(Integer.valueOf(v)));

            Optional.ofNullable(bulkConfig.get(BULK_FLUSH_BACKOFF_DELAY))
                    .ifPresent(v -> builder.setBulkFlushBackoffDelay(Long.valueOf(v)));
        }
        builder.setRestClientFactory(
                new DefaultRestClientFactory(
                        elasticsearch6Properties.getMaxRetryTimeoutMills(),
                        elasticsearch6Properties.getPathPrefix()));

        final ElasticsearchSink<Tuple2<Boolean, Row>> sink = builder.build();

        Optional.ofNullable(elasticsearch6Properties.getDisableFlushOnCheckpoint())
                .ifPresent(v -> {
                    if (Boolean.valueOf(v)) {
                        sink.disableFlushOnCheckpoint();
                    }
                });

        return sink;
    }

    private List<ElasticsearchUpsertTableSinkBase.Host> createHosts(List<String> hosts) {
        Preconditions.checkArgument(hosts != null && !hosts.isEmpty());
        List<ElasticsearchUpsertTableSinkBase.Host> esHosts = new ArrayList<>(hosts.size());
        for (String host : hosts) {
            String[] array = host.split(":");
            esHosts.add(new ElasticsearchUpsertTableSinkBase.Host(array[0], Integer.valueOf(array[1]), "http"));
        }
        return esHosts;
    }


    /**
     * Validate the types that are used for conversion to string.
     */
    private void validateKeyTypes(int[] keyFieldIndices) {
        final TypeInformation<?>[] types = getFieldTypes();
        for (int keyFieldIndex : keyFieldIndices) {
            final TypeInformation<?> type = types[keyFieldIndex];
            if (!TypeCheckUtils.isSimpleStringRepresentation(type)) {
                throw new ValidationException(
                        "Only simple types that can be safely converted into a string representation " +
                                "can be used as keys. But was: " + type);
            }
        }
    }

    static class DefaultRestClientFactory implements RestClientFactory {

        private Integer maxRetryTimeout;

        private String pathPrefix;

        public DefaultRestClientFactory(@Nullable Integer maxRetryTimeout, @Nullable String pathPrefix) {
            this.maxRetryTimeout = maxRetryTimeout;
            this.pathPrefix = pathPrefix;
        }

        @Override
        public void configureRestClientBuilder(RestClientBuilder restClientBuilder) {
            if (maxRetryTimeout != null) {
                restClientBuilder.setMaxRetryTimeoutMillis(maxRetryTimeout);
            }
            if (pathPrefix != null) {
                restClientBuilder.setPathPrefix(pathPrefix);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DefaultRestClientFactory that = (DefaultRestClientFactory) o;
            return Objects.equals(maxRetryTimeout, that.maxRetryTimeout) &&
                    Objects.equals(pathPrefix, that.pathPrefix);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    maxRetryTimeout,
                    pathPrefix);
        }
    }

    private static class Elasticsearch6RequestFactory implements ElasticsearchUpsertTableSinkBase.RequestFactory {

        @Override
        public UpdateRequest createUpdateRequest(
                String index,
                String docType,
                String key,
                XContentType contentType,
                byte[] document) {
            return new UpdateRequest(index, docType, key)
                    .doc(document, contentType)
                    .upsert(document, contentType);
        }

        @Override
        public IndexRequest createIndexRequest(
                String index,
                String docType,
                XContentType contentType,
                byte[] document) {
            return new IndexRequest(index, docType)
                    .source(document, contentType);
        }

        @Override
        public DeleteRequest createDeleteRequest(String index, String docType, String key) {
            return new DeleteRequest(index, docType, key);
        }
    }
}
