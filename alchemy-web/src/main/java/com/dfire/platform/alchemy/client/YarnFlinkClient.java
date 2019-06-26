package com.dfire.platform.alchemy.client;

import com.dfire.platform.alchemy.client.request.*;
import com.dfire.platform.alchemy.client.response.JobStatusResponse;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.client.response.SavepointResponse;
import com.dfire.platform.alchemy.client.response.SubmitFlinkResponse;
import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.yarn.client.api.YarnClient;

/**
 * todo 支持yarn mode
 * 
 * @author congbai
 * @date 2019/6/10
 */
public class YarnFlinkClient implements FlinkClient {

    private final YarnClient yarnClient;

    private final Configuration flinkConf;

    public YarnFlinkClient(YarnClient yarnClient, Configuration flinkConf) {
        this.yarnClient = yarnClient;
        this.flinkConf = flinkConf;
    }

    @Override
    public SavepointResponse cancel(CancelFlinkRequest cancelFlinkRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response rescale(RescaleFlinkRequest rescaleFlinkRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SavepointResponse savepoint(SavepointFlinkRequest savepointFlinkRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JobStatusResponse status(JobStatusRequest statusRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SubmitFlinkResponse submit(SubmitRequest submitRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWebInterfaceURL() {
        throw new UnsupportedOperationException();
    }
}
