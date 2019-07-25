package com.dfire.platform.alchemy.connectors.elasticsearch;

import com.dfire.platform.alchemy.connectors.common.util.ActionRequestFailureHandlerUtil;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.formats.json.JsonRowSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink;
import org.apache.flink.table.sinks.AppendStreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author congbai
 * @date 05/06/2018
 */
public class ElasticsearchTableSink implements AppendStreamTableSink<Row> {

    private final Elasticsearch5Properties elasticsearch5Properties;

    private String[] fieldNames;

    private TypeInformation[] fieldTypes;

    private JsonRowSerializationSchema jsonRowSchema;

    public ElasticsearchTableSink(Elasticsearch5Properties elasticsearch5Properties) {
        this.elasticsearch5Properties = elasticsearch5Properties;
    }

    @Override
    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(getFieldTypes());
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        ElasticsearchTableSink copy = new ElasticsearchTableSink(this.elasticsearch5Properties);
        copy.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        copy.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        Preconditions.checkArgument(fieldNames.length == fieldTypes.length,
                "Number of provided field names and types does not match.");

        RowTypeInfo rowSchema = new RowTypeInfo(fieldTypes, fieldNames);
        copy.jsonRowSchema = new JsonRowSerializationSchema(rowSchema);

        return copy;
    }

    private ElasticsearchSink<Row> createEsSink() {
        Map<String, String> userConfig = createUserConfig();
        List<InetSocketAddress> transports = new ArrayList<>();
        addTransportAddress(transports, this.elasticsearch5Properties.getTransports());
        ActionRequestFailureHandler actionRequestFailureHandler = ActionRequestFailureHandlerUtil.createFailureHandler(this.elasticsearch5Properties.getFailureHandler());
        Integer fieldIndex = findIndex(this.elasticsearch5Properties.getIndexField(), this.fieldNames);
        return new ElasticsearchSink<>(userConfig, transports,
                new ElasticsearchTableFunction(
                        this.elasticsearch5Properties.getIndex(),
                        fieldIndex,
                        this.elasticsearch5Properties.getIndexType(),
                        jsonRowSchema),
                actionRequestFailureHandler);
    }

    private Integer findIndex(String indexField, String[] fieldNames) {
        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(indexField)) {
                return i;
            }
        }
        return null;
    }

    private Map<String, String> createUserConfig() {
        Map<String, String> userConfig = new HashMap<>();
        userConfig.put("cluster.name", this.elasticsearch5Properties.getClusterName());
        Map<String, Object> config = this.elasticsearch5Properties.getConfig();
        if (config == null) {
            return userConfig;
        }
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            userConfig.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return userConfig;
    }

    private void addTransportAddress(List<InetSocketAddress> transports, String serverList) {
        for (String server : serverList.split(",")) {
            try {
                String[] array = server.split(":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                transports.add(new InetSocketAddress(InetAddress.getByName(host), port));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        ElasticsearchSink<Row> elasticsearchSink = createEsSink();
        dataStream.addSink(elasticsearchSink);
    }
}
