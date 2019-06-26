package com.dfire.platform.alchemy.connectors.tsdb.handler;

import com.aliyun.hitsdb.client.HiTSDB;
import com.aliyun.hitsdb.client.HiTSDBClientFactory;
import com.aliyun.hitsdb.client.HiTSDBConfig;
import com.aliyun.hitsdb.client.callback.BatchPutCallback;
import com.aliyun.hitsdb.client.value.Result;
import com.aliyun.hitsdb.client.value.request.Point;
import com.dfire.platform.alchemy.connectors.tsdb.TsdbData;
import com.dfire.platform.alchemy.connectors.tsdb.TsdbProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author congbai
 * @date 2018/8/8
 */
public class HitsdbHandler implements TsdbHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HitsdbHandler.class);

    private transient HiTSDB tsdb;

    public HitsdbHandler(TsdbProperties opentsdbProperties) {
        HiTSDBConfig.Builder builder = HiTSDBConfig
            // 配置地址，第一个参数可以是域名，IP。
            .address(opentsdbProperties.getUrl(), 8242)
            // 异步写相关，异步批量 Put 回调接口。
            .listenBatchPut(new BatchPutCallback() {
                @Override
                public void response(String s, List<Point> list, Result result) {
                    // nothing to do
                    LOG.info("处理成功:{},result:{}", list.size(), result.toJSON());
                }

                @Override
                public void failed(String address, List<Point> input, Exception ex) {
                    ex.printStackTrace();
                    LOG.error("失败处理:{}", input.size(), ex);
                }
            })
            // 流量限制。设置每秒最大提交Point的个数。
            .maxTPS(50000);
        if (opentsdbProperties.getIoThreadCount() != null) {
            builder.ioThreadCount(opentsdbProperties.getIoThreadCount());
        }
        if (opentsdbProperties.getBatchPutBufferSize() != null) {
            builder.batchPutBufferSize(opentsdbProperties.getBatchPutBufferSize());
        }
        if (opentsdbProperties.getBatchPutConsumerThreadCount() != null) {
            builder.batchPutConsumerThreadCount(opentsdbProperties.getBatchPutConsumerThreadCount());
        }
        if (opentsdbProperties.getBatchPutSize() != null) {
            builder.batchPutSize(opentsdbProperties.getBatchPutSize());
        }
        if (opentsdbProperties.getBatchPutTimeLimit() != null) {
            builder.batchPutTimeLimit(opentsdbProperties.getBatchPutTimeLimit());
        }
        if (opentsdbProperties.getPutRequestLimit() != null) {
            builder.putRequestLimit(opentsdbProperties.getPutRequestLimit());
        }
        this.tsdb = HiTSDBClientFactory.connect(builder.config());
    }

    @Override
    public void execute(TsdbData tsdbData) {
        if (tsdbData.getMetricValues() == null || tsdbData.getMetricValues().isEmpty() || tsdbData.getTags() == null
            || tsdbData.getTags().isEmpty()) {
            return;
        }
        tsdbData.getMetricValues().entrySet().forEach((entry) -> {
            Point point = Point.metric(entry.getKey()).timestamp(tsdbData.getTimestamp()).tag(tsdbData.getTags())
                .value(entry.getValue()).build();
            this.tsdb.put(point);
        });
    }

    @Override
    public void close() throws IOException {
        this.tsdb.close();
    }
}
