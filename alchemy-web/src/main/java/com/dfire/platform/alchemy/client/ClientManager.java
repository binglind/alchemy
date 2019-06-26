package com.dfire.platform.alchemy.client;

import com.dfire.platform.alchemy.client.loader.JarLoader;
import com.dfire.platform.alchemy.domain.Cluster;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author congbai
 * @date 04/06/2018
 */
@Component
public class ClientManager {

    private final JarLoader jarLoader;

    private final Map<Long, FlinkClient> clusterClients = new ConcurrentHashMap<>();

    public ClientManager(JarLoader jarLoader) {
        this.jarLoader = jarLoader;
    }

    public FlinkClient getClient(Long clusterId) {
        return clusterClients.get(clusterId);
    }

    public void putClient(Cluster cluster) throws Exception {
        FlinkClient client = ClusterClientFactory.get(cluster, jarLoader);
        clusterClients.put(cluster.getId(), client);
    }

    public void deleteClient(Long clusterId) {
        clusterClients.remove(clusterId);
    }

}
