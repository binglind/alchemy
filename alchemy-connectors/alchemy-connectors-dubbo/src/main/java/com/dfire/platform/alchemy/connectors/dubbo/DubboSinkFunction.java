package com.dfire.platform.alchemy.connectors.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.dfire.platform.alchemy.connectors.common.MetricFunction;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.Map;

/**
 * only support Map param
 * @author congbai
 * @date 2018/7/10
 */
public class DubboSinkFunction extends RichSinkFunction<Row> implements MetricFunction {

    private static final long serialVersionUID = 1L;

    private static final String DUBBO_METRICS_GROUP = "Dubbo";

    private final DubboProperties dubboProperties;

    private final String [] fieldNames;

    private final String[] dubboParameterTypes;

    private final String method;

    private ReferenceConfig<GenericService> reference;

    private Counter numRecordsOut;

    public DubboSinkFunction(DubboProperties dubboProperties, String[] fieldNames) {
        this.dubboProperties = dubboProperties;
        this.method = dubboProperties.getMethodName();
        this.fieldNames = fieldNames;
        this.dubboParameterTypes = new String[]{String.class.getName(), Map.class.getName()};
    }

    private ReferenceConfig<GenericService> referenceConfig(DubboProperties properties) throws Exception {
        ApplicationConfig application = new ApplicationConfig();
        application.setName(properties.getApplicationName());
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(new RegistryConfig(properties.getRegistryAddr()));
        reference.setInterface(properties.getInterfaceName());
        reference.setVersion(properties.getVersion());
        reference.setGeneric(true);
        reference.setCheck(false);
        reference.setInit(true);
        if(properties.getProperties() != null){
            BeanUtils.copyProperties(reference, properties.getProperties());
        }
        return reference;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.reference = referenceConfig(this.dubboProperties);
    }


    @Override
    public void close() throws Exception {
        super.close();
        reference.destroy();
    }

    @Override
    public void invoke(Row value, Context context) {
        GenericService genericService = reference.get();
        Map<String, Object> values = createValue(value);
        genericService.$invoke(method, dubboParameterTypes, new Object[]{dubboProperties.getUniqueName(), values});
        numRecordsOut = createOrGet(numRecordsOut, getRuntimeContext());
        numRecordsOut.inc();
    }

    private Map<String, Object> createValue(Row row) {
        int arity = row.getArity();
        Map<String, Object> values = new HashMap<>(arity);
        for(int i = 0 ; i < arity ; i++){
             Object value = row.getField(i);
             if(value == null){
                 continue;
             }
             values.put(fieldNames[i], value);
        }
        return values;
    }


    @Override
    public String metricGroupName() {
        return DUBBO_METRICS_GROUP;
    }
}
