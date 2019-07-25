package com.dfire.platform.alchemy.client;

import com.dfire.platform.alchemy.client.loader.JarLoader;
import com.dfire.platform.alchemy.client.openshift.OpenshiftWebUrlCache;
import com.dfire.platform.alchemy.domain.Cluster;
import com.dfire.platform.alchemy.domain.enumeration.ClusterType;
import com.dfire.platform.alchemy.service.dto.ClusterDTO;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;

import java.util.List;
import java.util.Map;

/**
 * @author congbai
 * @date 2019/5/15
 */
public class ClusterClientFactory {

    public static FlinkClient get(ClusterDTO cluster, JarLoader jarLoader, String webUrl) throws Exception {
        ClusterType clusterType = cluster.getType();
        switch (clusterType) {
            case STANDALONE:
                StandaloneClusterInfo clusterInfo = BindPropertiesUtil.bindProperties(cluster.getConfig(), StandaloneClusterInfo.class);
                return createRestClient(clusterInfo, jarLoader);
            case OPENSHIFT:
                OpenshiftClusterInfo openshiftClusterInfo = BindPropertiesUtil.bindProperties(cluster.getConfig(), OpenshiftClusterInfo.class);
                return createOpenshiftClusterClient(openshiftClusterInfo, jarLoader, webUrl);
            case YARN:
                // todo 支持yarn client
            default:
                throw new UnsupportedOperationException("only support rest client ");
        }
    }

    private static FlinkClient createOpenshiftClusterClient(OpenshiftClusterInfo openshiftClusterInfo, JarLoader jarLoader, String url) {
        Configuration configuration = new Configuration();
        setProperties(openshiftClusterInfo.getConfigs(), configuration);
        configuration.setString(JobManagerOptions.ADDRESS, openshiftClusterInfo.getJobManagerAddress());
        return createClient(configuration, jarLoader, openshiftClusterInfo.getDependencies(), url);
    }

    public static FlinkClient createRestClient(StandaloneClusterInfo clusterInfo, JarLoader jarLoader) throws Exception {
        Configuration configuration = new Configuration();
        if (clusterInfo.getProperties() != null) {
            setProperties(clusterInfo.getProperties(), configuration);
        }
        configuration.setString(JobManagerOptions.ADDRESS, clusterInfo.getAddress());
        configuration.setInteger(JobManagerOptions.PORT, clusterInfo.getPort());
        configuration.setInteger(RestOptions.PORT, clusterInfo.getPort());
        return createClient(configuration, jarLoader, clusterInfo.getDependencies(), clusterInfo.getWebInterfaceUrl());
    }

    private static FlinkClient createClient(Configuration configuration, JarLoader jarLoader, List<String> dependencies, String webUrl) {
        try {
            RestClusterClient restClient = new RestClusterClient<>(configuration, "RemoteExecutor");
            restClient.setPrintStatusDuringExecution(true);
            restClient.setDetached(true);
            return new StandaloneClusterFlinkClient(restClient, jarLoader, dependencies, webUrl);
        } catch (Exception e) {
            throw new RuntimeException("Cannot establish connection to JobManager: " + e.getMessage(), e);
        }
    }

    private static void setProperties(Map<String, Object> properties, Configuration configuration) {
        properties.entrySet().forEach(property -> {
            Object value = property.getValue();
            if (value instanceof String) {
                configuration.setString(property.getKey(), value.toString());
            } else if (value instanceof Boolean) {
                configuration.setBoolean(property.getKey(), (Boolean) value);
            } else if (value instanceof Long) {
                configuration.setLong(property.getKey(), (Long) value);
            } else if (value instanceof Float) {
                configuration.setFloat(property.getKey(), (Float) value);
            } else if (value instanceof Integer) {
                configuration.setInteger(property.getKey(), (Integer) value);
            } else if (value instanceof Double) {
                configuration.setDouble(property.getKey(), (Double) value);
            } else {
                configuration.setString(property.getKey(), value.toString());
            }
        });
    }

}
