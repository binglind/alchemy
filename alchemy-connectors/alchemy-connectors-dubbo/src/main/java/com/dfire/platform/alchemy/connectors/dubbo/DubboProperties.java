package com.dfire.platform.alchemy.connectors.dubbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author congbai
 * @date 2018/7/12
 */
public class DubboProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务的唯一名称
     */
    private String uniqueName;

    private String applicationName;

    private String interfaceName;

    private String methodName;

    private String version;

    private String registryAddr;

    private Properties properties;

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRegistryAddr() {
        return registryAddr;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
