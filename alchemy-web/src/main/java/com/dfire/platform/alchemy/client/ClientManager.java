package com.dfire.platform.alchemy.client;

import com.dfire.platform.alchemy.client.loader.JarLoader;
import com.dfire.platform.alchemy.domain.enumeration.ClusterType;
import com.dfire.platform.alchemy.service.OpenshiftService;
import com.dfire.platform.alchemy.service.dto.ClusterDTO;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.apache.flink.table.expressions.E;
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

    private final OpenshiftService openshiftService;

    public ClientManager(JarLoader jarLoader, OpenshiftService openshiftService) {
        this.jarLoader = jarLoader;
        this.openshiftService = openshiftService;
    }

    public FlinkClient getClient(Long clusterId) {
        return clusterClients.get(clusterId);
    }

    public void addClientOnly(ClusterDTO cluster) throws Exception {
        String url = findWebUrl(cluster);
        FlinkClient client = ClusterClientFactory.get(cluster, jarLoader, url);
        clusterClients.put(cluster.getId(), client);
    }

    public void putClient(ClusterDTO cluster) throws Exception {
        addCluster(cluster);
        String url = findWebUrl(cluster);
        FlinkClient client = ClusterClientFactory.get(cluster, jarLoader, url);
        clusterClients.put(cluster.getId(), client);
    }

    public void updateClient(ClusterDTO cluster) throws Exception {
        updateCluster(cluster);
        String url = findWebUrl(cluster);
        FlinkClient client = ClusterClientFactory.get(cluster, jarLoader, url);
        clusterClients.put(cluster.getId(), client);
    }

    public void deleteClient(ClusterDTO cluster) throws Exception {
        deleteCluster(cluster);
        clusterClients.remove(cluster.getId());
    }

    private void addCluster(ClusterDTO cluster) throws Exception {
        ClusterType clusterType = cluster.getType();
        if (clusterType == ClusterType.OPENSHIFT) {
            OpenshiftClusterInfo openshiftClusterInfo = bind(cluster);
            try {
                openshiftService.create(openshiftClusterInfo);
            }catch (Exception e){
                openshiftService.delete(openshiftClusterInfo);
                throw e;
            }
        }
    }

    private void updateCluster(ClusterDTO cluster) throws Exception {
        ClusterType clusterType = cluster.getType();
        if (clusterType == ClusterType.OPENSHIFT) {
            OpenshiftClusterInfo openshiftClusterInfo = bind(cluster);
            openshiftService.update(openshiftClusterInfo);
        }
    }

    private void deleteCluster(ClusterDTO cluster) throws Exception {
        ClusterType clusterType = cluster.getType();
        if (clusterType == ClusterType.OPENSHIFT) {
            OpenshiftClusterInfo openshiftClusterInfo = bind(cluster);
            openshiftService.delete(openshiftClusterInfo);
        }
    }

    private String findWebUrl(ClusterDTO toDto) throws Exception {
        ClusterType clusterType = toDto.getType();
        if (clusterType == ClusterType.OPENSHIFT) {
            return openshiftService.queryWebUrl(toDto);
        }else {
            return null;
        }
    }

    private OpenshiftClusterInfo bind(ClusterDTO clusterDTO) throws Exception {
        return BindPropertiesUtil.bindProperties(clusterDTO.getConfig(), OpenshiftClusterInfo.class);
    }

}
