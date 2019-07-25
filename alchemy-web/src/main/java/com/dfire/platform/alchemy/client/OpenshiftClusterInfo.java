package com.dfire.platform.alchemy.client;

import java.util.List;
import java.util.Map;

public class OpenshiftClusterInfo {

    /**
     * cluster name
     */
    private String name;

    /**
     * openshift image
     */
    private String image;

    private String jobManagerAddress;

    /**
     * taskManager的pod数
     */
    private int replicas;

    private Resources jobManagerResources;

    private Resources taskManagerResources;

    /**
     * pod运行的环境变量
     */
    private Map<String, String> envs;

    /**
     * 启动flink进程时添加的参数，如high-availability.storageDir /flink/ha
     */
    private Map<String, Object> configs;

    /**
     * 集群的外部依赖
     */
    private List<String> dependencies;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public Map<String, String> getEnvs() {
        return envs;
    }

    public void setEnvs(Map<String, String> envs) {
        this.envs = envs;
    }

    public Resources getJobManagerResources() {
        return jobManagerResources;
    }

    public void setJobManagerResources(Resources jobManagerResources) {
        this.jobManagerResources = jobManagerResources;
    }

    public Resources getTaskManagerResources() {
        return taskManagerResources;
    }

    public void setTaskManagerResources(Resources taskManagerResources) {
        this.taskManagerResources = taskManagerResources;
    }

    public Map<String, Object> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, Object> configs) {
        this.configs = configs;
    }

    public String getJobManagerAddress() {
        return jobManagerAddress;
    }

    public void setJobManagerAddress(String jobManagerAddress) {
        this.jobManagerAddress = jobManagerAddress;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public static class Label {

        private String app;

        private String component;

        public Label() {
        }

        public Label(String app, String component) {
            this.app = app;
            this.component = component;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }
    }

    public static class Resources {

        private Resource limits;

        private Resource requests;

        public Resources() {
        }

        public Resources(Resource requests, Resource limits) {
            this.limits = limits;
            this.requests = requests;
        }

        public Resource getLimits() {
            return limits;
        }

        public void setLimits(Resource limits) {
            this.limits = limits;
        }

        public Resource getRequests() {
            return requests;
        }

        public void setRequests(Resource requests) {
            this.requests = requests;
        }
    }

    public static class Resource {

        private String cpu;

        private String memory;

        public Resource() {
        }

        public Resource(String cpu, String memory) {
            this.cpu = cpu;
            this.memory = memory;
        }

        public String getCpu() {
            return cpu;
        }

        public void setCpu(String cpu) {
            this.cpu = cpu;
        }

        public String getMemory() {
            return memory;
        }

        public void setMemory(String memory) {
            this.memory = memory;
        }
    }
}
