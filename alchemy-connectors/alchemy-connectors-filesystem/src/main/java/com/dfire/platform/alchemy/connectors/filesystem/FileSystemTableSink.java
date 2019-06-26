package com.dfire.platform.alchemy.connectors.filesystem;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.fs.SequenceFileWriter;
import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;
import org.apache.flink.streaming.connectors.fs.bucketing.DateTimeBucketer;
import org.apache.flink.table.sinks.AppendStreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.StringUtils;
import org.apache.hadoop.io.SequenceFile;

/**
 * @author congbai
 * @date 2018/7/10
 */
public class FileSystemTableSink implements AppendStreamTableSink<Row> {

    private final FilePropereties filePropereties;

    private String[] fieldNames;

    private TypeInformation[] fieldTypes;

    public FileSystemTableSink(FilePropereties filePropereties) {
        check(filePropereties);
        this.filePropereties = Preconditions.checkNotNull(filePropereties, "filePropereties");
    }

    private void check(FilePropereties filePropereties) {
        Preconditions.checkNotNull(filePropereties.getBasePath(), "basePath can'be null");
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
    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(this.fieldTypes);
    }

    @Override
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        FileSystemTableSink copy = new FileSystemTableSink(this.filePropereties);
        copy.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        copy.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        Preconditions.checkArgument(fieldNames.length == fieldTypes.length,
            "Number of provided field names and types does not match.");
        return copy;
    }

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        RichSinkFunction richSinkFunction = createFileRich();
        dataStream.addSink(richSinkFunction);
    }

    private RichSinkFunction createFileRich() {
        final  FilePropereties filePropereties = this.filePropereties;
        BucketingSink sink = new FileSink(filePropereties.getBasePath());
        if (!StringUtils.isNullOrWhitespaceOnly(filePropereties.getDateFormat())){
            sink.setBucketer(new DateTimeBucketer<String>(filePropereties.getDateFormat()));
        }
        if (filePropereties.getBatchSize() != null){
             sink.setBatchSize(filePropereties.getBatchSize());
        }
        if (filePropereties.getInactiveBucketCheckInterval() != null){
            sink.setInactiveBucketCheckInterval(filePropereties.getInactiveBucketCheckInterval());
        }
        if (filePropereties.getAsyncTimeout() != null){
            sink.setAsyncTimeout(filePropereties.getAsyncTimeout());
        }
        if (filePropereties.getInactiveBucketThreshold() != null){
            sink.setInactiveBucketThreshold(filePropereties.getInactiveBucketThreshold());
        }
        if (filePropereties.getInProgressPrefix() != null){
            sink.setInProgressPrefix(filePropereties.getInProgressPrefix());
        }
        if (filePropereties.getInProgressSuffix() != null){
            sink.setInProgressSuffix(filePropereties.getInProgressSuffix());
        }
        if (filePropereties.getPartPrefix() != null){
            sink.setPartPrefix(filePropereties.getPartPrefix());
        }
        if (filePropereties.getPartSuffix() != null){
            sink.setPartSuffix(filePropereties.getPartSuffix());
        }
        if (filePropereties.getPendingPrefix() != null){
            sink.setPendingPrefix(filePropereties.getPendingPrefix());
        }
        if (filePropereties.getPendingSuffix() != null){
            sink.setPendingSuffix(filePropereties.getPendingSuffix());
        }
        if (filePropereties.getUseTruncate() != null){
            sink.setUseTruncate(filePropereties.getUseTruncate());
        }
        if (filePropereties.getValidLengthPrefix() != null){
            sink.setValidLengthPrefix(filePropereties.getValidLengthPrefix());
        }
        if (filePropereties.getValidLengthSuffix() != null){
            sink.setValidLengthSuffix(filePropereties.getValidLengthSuffix());
        }
        Writer writer = filePropereties.getWriter();
        if (writer != null){
            SequenceFileWriter sequenceFileWriter;
            if (writer.getCompressionCodecName() != null){
                sequenceFileWriter = new SequenceFileWriter(writer.getCompressionCodecName(), SequenceFile.CompressionType.valueOf(writer.getCompressionType().toUpperCase()));
            }else{
                 sequenceFileWriter = new SequenceFileWriter<>();
            }
            sequenceFileWriter.setInputType(getOutputType(), null);
            sink.setWriter(sequenceFileWriter);
        }

        return sink;
    }
}
