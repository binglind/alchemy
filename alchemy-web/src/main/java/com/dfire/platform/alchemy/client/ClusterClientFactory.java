package com.dfire.platform.alchemy.client;

import com.dfire.platform.alchemy.client.loader.JarLoader;
import com.dfire.platform.alchemy.domain.Cluster;
import com.dfire.platform.alchemy.domain.enumeration.ClusterType;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.StandaloneClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.JobManagerOptions;

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
        configuration.setString(HighAvailabilityOptions.HA_MODE, clusterInfo.getMode());
        if (StringUtils.isNotEmpty(clusterInfo.getClusterId())){
            configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, clusterInfo.getClusterId());
        }
        if (StringUtils.isNotEmpty(clusterInfo.getZookeeperQuorum())){
            configuration.setString(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM, clusterInfo.getZookeeperQuorum());
        }
        if (StringUtils.isNotEmpty(clusterInfo.getStoragePath())){
            configuration.setString(HighAvailabilityOptions.HA_STORAGE_PATH, clusterInfo.getStoragePath());
        }
        if (StringUtils.isNotEmpty(clusterInfo.getAddress())){
            configuration.setString(JobManagerOptions.ADDRESS, clusterInfo.getAddress());
        }
        if (StringUtils.isNotEmpty(clusterInfo.getLookupTimeout())){
            configuration.setString(JobManagerOptions.ADDRESS, clusterInfo.getAddress());
        }
        if (clusterInfo.getPort() != null){
            configuration.setInteger(JobManagerOptions.PORT, clusterInfo.getPort());
        }
        try {
            StandaloneClusterClient clusterClient = new StandaloneClusterClient(configuration);
            clusterClient.setPrintStatusDuringExecution(true);
            clusterClient.setDetached(true);
            return new StandaloneClusterFlinkClient(clusterClient, jarLoader,  clusterInfo.getDependencies(), clusterInfo.getWebInterfaceUrl());
        } catch (Exception e) {
            throw new RuntimeException("Cannot establish connection to JobManager: " + e.getMessage(), e);
        }
    }

}
