package com.dfire.platform.alchemy.client;

import com.dfire.platform.alchemy.client.loader.JarLoader;
import com.dfire.platform.alchemy.client.request.*;
import com.dfire.platform.alchemy.client.response.JobStatusResponse;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.client.response.SavepointResponse;
import com.dfire.platform.alchemy.client.response.SubmitFlinkResponse;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.table.shaded.org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author congbai
 * @date 2019/6/10
 */
public class StandaloneClusterFlinkClient extends AbstractFlinkClient {

    private final ClusterClient clusterClient;

    private final String webInterfaceURL;

    public StandaloneClusterFlinkClient(ClusterClient clusterClient, JarLoader jarLoader,
                                        List<String> dependencies, String webInterfaceURL) {
        super(jarLoader, dependencies);
        this.clusterClient = clusterClient;
        this.webInterfaceURL = webInterfaceURL;
    }

    @Override
    public SavepointResponse cancel(CancelFlinkRequest request) throws Exception {
        return cancel(clusterClient, request);
    }

    @Override
    public Response rescale(RescaleFlinkRequest request) throws Exception {
        return rescale(clusterClient, request);
    }

    @Override
    public SavepointResponse savepoint(SavepointFlinkRequest request) throws Exception {
        return savepoint(clusterClient, request);
    }

    @Override
    public JobStatusResponse status(JobStatusRequest request) throws Exception {
        return status(clusterClient, request);
    }

    @Override
    public SubmitFlinkResponse submit(SubmitRequest request) throws Exception {
        if (request instanceof JarSubmitFlinkRequest) {
            return submitJar(clusterClient, (JarSubmitFlinkRequest)request);
        } else if (request instanceof SqlSubmitFlinkRequest) {
            return submitSql(clusterClient, (SqlSubmitFlinkRequest)request);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWebInterfaceURL() {
        if(StringUtils.isEmpty(this.webInterfaceURL)){
            return clusterClient.getWebInterfaceURL();
        }else{
            return this.webInterfaceURL;
        }
    }
}
