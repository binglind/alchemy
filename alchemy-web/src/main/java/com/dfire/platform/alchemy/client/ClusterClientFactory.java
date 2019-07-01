package com.dfire.platform.alchemy.client;

import com.dfire.platform.alchemy.client.loader.JarLoader;
import com.dfire.platform.alchemy.domain.Cluster;
import com.dfire.platform.alchemy.domain.enumeration.ClusterType;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;

/**
 * @author congbai
 * @date 2019/5/15
 */
public class ClusterClientFactory {

    public static FlinkClient get(Cluster cluster, JarLoader jarLoader) throws Exception {
        ClusterType clusterType = cluster.getType();
        switch (clusterType) {
            case STANDALONE:
                StandaloneClusterInfo clusterInfo = BindPropertiesUtil.bindProperties(cluster.getConfig(), StandaloneClusterInfo.class);
                return createRestClient(clusterInfo, jarLoader);
            case YARN:
                // todo 支持yarn client
            default:
                throw new UnsupportedOperationException("only support rest client ");
        }
    }

    public static FlinkClient createRestClient(StandaloneClusterInfo clusterInfo, JarLoader jarLoader) throws Exception {
        Configuration configuration = new Configuration();
        if(clusterInfo.getProperties() != null){
            clusterInfo.getProperties().entrySet().forEach(property ->{
                Object value = property.getValue();
                if(value instanceof String) {
                    configuration.setString(property.getKey(), value.toString());
                }else if(value instanceof Boolean){
                    configuration.setBoolean(property.getKey(), (Boolean) value);
                }else if(value instanceof Long){
                    configuration.setLong(property.getKey(), (Long) value);
                }else if(value instanceof Float){
                    configuration.setFloat(property.getKey(), (Float) value);
                }else if(value instanceof Integer){
                    configuration.setInteger(property.getKey(), (Integer) value);
                }else if(value instanceof Double){
                    configuration.setDouble(property.getKey(), (Double) value);
                }else{
                    configuration.setString(property.getKey(), value.toString());
                }
            });
        }
        configuration.setString(JobManagerOptions.ADDRESS, clusterInfo.getAddress());
        configuration.setInteger(JobManagerOptions.PORT, clusterInfo.getPort());
        configuration.setInteger(RestOptions.PORT, clusterInfo.getPort());
        try {
            RestClusterClient restClient =  new RestClusterClient<>(configuration, "RemoteExecutor");
            restClient.setPrintStatusDuringExecution(true);
            restClient.setDetached(true);
            return new StandaloneClusterFlinkClient(restClient, jarLoader,  clusterInfo.getDependencies(), clusterInfo.getWebInterfaceUrl());
        } catch (Exception e) {
            throw new RuntimeException("Cannot establish connection to JobManager: " + e.getMessage(), e);
        }
    }

}
