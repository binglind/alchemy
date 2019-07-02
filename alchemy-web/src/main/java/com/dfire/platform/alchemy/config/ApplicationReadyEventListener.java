package com.dfire.platform.alchemy.config;

import com.dfire.platform.alchemy.client.ClientManager;
import com.dfire.platform.alchemy.domain.Cluster;
import com.dfire.platform.alchemy.repository.ClusterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationReadyEventListener.class);

    private final ClientManager clientManager;

    private final ClusterRepository clusterRepository;

    public ApplicationReadyEventListener(ClientManager clientManager, ClusterRepository clusterRepository) {
        this.clientManager = clientManager;
        this.clusterRepository = clusterRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<Cluster> clusterList = clusterRepository.findAll();
        clusterList.forEach(cluster -> {
            try {
                clientManager.putClient(cluster);
            } catch (Exception e) {
                LOGGER.error("Init Cluster Exception", e);
            }
        });
    }
}
