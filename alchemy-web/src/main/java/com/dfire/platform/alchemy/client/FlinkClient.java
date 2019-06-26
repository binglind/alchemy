package com.dfire.platform.alchemy.client;

import com.dfire.platform.alchemy.client.request.*;
import com.dfire.platform.alchemy.client.response.JobStatusResponse;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.client.response.SavepointResponse;
import com.dfire.platform.alchemy.client.response.SubmitFlinkResponse;

/**
 * @author congbai
 * @date 2019/6/4
 */
public interface FlinkClient {

    SavepointResponse cancel(CancelFlinkRequest request) throws Exception;

    Response rescale(RescaleFlinkRequest request) throws Exception;

    SavepointResponse savepoint(SavepointFlinkRequest request) throws Exception;

    JobStatusResponse status(JobStatusRequest request) throws Exception;

    SubmitFlinkResponse submit(SubmitRequest request) throws Exception;

    String getWebInterfaceURL();

}
