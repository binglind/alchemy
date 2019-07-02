package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.connectors.dubbo.DubboProperties;
import com.dfire.platform.alchemy.connectors.dubbo.DubboTableSink;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.Properties;

/**
 * @author congbai
 * @date 03/06/2018
 */
public class DubboSinkDescriptor extends SinkDescriptor {

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

    @Override
    public <T> T transform() throws Exception {
        DubboProperties properties = new DubboProperties();
        BeanUtils.copyProperties(this, properties);
        return (T) new DubboTableSink(properties);
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(applicationName, "applicationName must be not null");
        Assert.notNull(interfaceName, "interfaceName must be not null");
        Assert.notNull(methodName, "methodName must be not null");
        Assert.notNull(registryAddr, "registryAddr must be not null");
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_DUBBO;
    }

}
