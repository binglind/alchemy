package com.dfire.platform.alchemy.service;

import com.dfire.platform.alchemy.client.OpenshiftClusterInfo;
import com.dfire.platform.alchemy.config.OpenshiftProperties;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenshiftServiceIT {

    OpenshiftService openshiftService;

    @Before
    public void before() throws IOException, InterruptedException {
        OpenshiftProperties openshiftProperties = new OpenshiftProperties();
        openshiftProperties.setUsername("yourname");
        openshiftProperties.setPassword("yourpassword");
        openshiftProperties.setNamespace("flink");
        openshiftProperties.setHadoopUserName("hdfs");
        openshiftProperties.setHadoopVolumeName("hadoop");
        openshiftProperties.setServiceAccount("flink");
        openshiftProperties.setServiceAccountName("flink");
        openshiftProperties.setUrl("https://console-openshift-console.apps.us-east-1.online-starter.openshift.com");
        openshiftService = new OpenshiftService(new RestTemplate(), openshiftProperties);
        long startTimestamp = System.currentTimeMillis();
        while (openshiftService.getToken() == null){
            Thread.sleep(10);
            if(System.currentTimeMillis() - startTimestamp > 10000){
                throw new RuntimeException("Failed to get Token");
            }
        }
    }


    @Test
    public void create() {
        OpenshiftClusterInfo openshiftClusterInfo = createInfo();
        openshiftService.create(openshiftClusterInfo);
    }

    @Test
    public void update() {
        OpenshiftClusterInfo openshiftClusterInfo = createInfo();
        openshiftClusterInfo.setReplicas(5);
        openshiftClusterInfo.setJobManagerResources(new OpenshiftClusterInfo.Resources(new OpenshiftClusterInfo.Resource("2", "4G"), new OpenshiftClusterInfo.Resource("3", "8G")));
        openshiftClusterInfo.setTaskManagerResources(new OpenshiftClusterInfo.Resources(new OpenshiftClusterInfo.Resource("500m", "2G"), new OpenshiftClusterInfo.Resource("1", "3G")));
        openshiftService.update(openshiftClusterInfo);
    }

    @Test
    public void delete() {
        OpenshiftClusterInfo openshiftClusterInfo = createInfo();
        openshiftService.delete(openshiftClusterInfo);
    }


    private OpenshiftClusterInfo createInfo() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("high-availability.jobmanager.port", "6123");
        configs.put("high-availability", "zookeeper");
        configs.put("high-availability.storageDir", "/flink/ha/default");
        configs.put("high-availability.cluster-id", "test");
        configs.put("high-availability.zookeeper.quorum", "127.0.01");
        OpenshiftClusterInfo openshiftClusterInfo = new OpenshiftClusterInfo();
        openshiftClusterInfo.setImage("dongbl1114/docker-flink:1.8.0-alchemy");
        openshiftClusterInfo.setName("magiceye");
        openshiftClusterInfo.setJobManagerAddress("jobmanager-magiceye");
        openshiftClusterInfo.setReplicas(2);
        openshiftClusterInfo.setConfigs(configs);
        openshiftClusterInfo.setJobManagerResources(new OpenshiftClusterInfo.Resources(new OpenshiftClusterInfo.Resource("1", "3G"), new OpenshiftClusterInfo.Resource("3", "8G")));
        openshiftClusterInfo.setTaskManagerResources(new OpenshiftClusterInfo.Resources(new OpenshiftClusterInfo.Resource("300m", "2G"), new OpenshiftClusterInfo.Resource("1", "3G")));
        return openshiftClusterInfo;
    }

}
