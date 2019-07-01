package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.api.function.StreamAggregateFunction;
import com.dfire.platform.alchemy.api.function.StreamScalarFunction;
import com.dfire.platform.alchemy.api.function.StreamTableFunction;
import com.dfire.platform.alchemy.api.function.aggregate.FlinkAllAggregateFunction;
import com.dfire.platform.alchemy.api.function.scalar.FlinkAllScalarFunction;
import com.dfire.platform.alchemy.api.function.table.FlinkAllTableFunction;
import com.dfire.platform.alchemy.api.util.GroovyCompiler;
import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.domain.Udf;
import com.dfire.platform.alchemy.domain.enumeration.UdfType;
import com.dfire.platform.alchemy.util.ClassUtil;
import com.dfire.platform.alchemy.util.ThreadLocalClassLoader;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * 描述用户自定义函数
 *
 * @author congbai
 * @date 01/06/2018
 */
public class UdfDescriptor<R> implements CoreDescriptor<R> {

    private String name;

    /**
     * 如果是code读取方式，value就是代码内容 ；如果是jar包方式，则是className
     */
    private String value;

    private UdfType udfType;

    private String dependency;

    public static UdfDescriptor from(Udf udf) {
        UdfDescriptor udfDescriptor = new UdfDescriptor();
        BeanUtils.copyProperties(udf, udfDescriptor);
        udfDescriptor.setName(udf.getName());
        udfDescriptor.setUdfType(udf.getType());
        return udfDescriptor;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UdfType getUdfType() {
        return udfType;
    }

    public void setUdfType(UdfType udfType) {
        this.udfType = udfType;
    }

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    @Override
    public <T> T transform() throws Exception {
        Class clazz;
        if (UdfType.CODE == udfType) {
            clazz = GroovyCompiler.compile(this.getValue(), this.getName());
        } else {
            clazz = ClassUtil.forName(value);
        }
        try {
            Object udf = clazz.newInstance();
            if (udf instanceof StreamScalarFunction) {
                StreamScalarFunction<?> streamScalarFunction = (StreamScalarFunction<?>)udf;
                FlinkAllScalarFunction scalarFunction = new FlinkAllScalarFunction(this.getValue(), this.getName());
                initScalarFuntion(streamScalarFunction, scalarFunction);
                return (T)scalarFunction;
            } else if (udf instanceof StreamTableFunction<?>) {
                StreamTableFunction<?> streamTableFunction = (StreamTableFunction)udf;
                FlinkAllTableFunction tableFunction = new FlinkAllTableFunction(this.getValue(), this.getName());
                initTableFunction(streamTableFunction, tableFunction);
                return (T)tableFunction;
            } else if (udf instanceof StreamAggregateFunction<?, ?, ?>) {
                StreamAggregateFunction<?, ?, ?> streamAggregateFunction = (StreamAggregateFunction)udf;
                FlinkAllAggregateFunction aggregateFunction
                    = new FlinkAllAggregateFunction(this.getValue(), this.getName());
                initAggregateFuntion(streamAggregateFunction, aggregateFunction);
                return (T)aggregateFunction;
            } else {
                return (T)udf;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid UDF " + this.getName(), ex);
        }
    }


    @Override
    public String type() {
        return Constants.TYPE_VALUE_UDF;
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(name, "函数名不能为空");
    }

    private void initScalarFuntion(StreamScalarFunction<?> streamScalarFunction,
        FlinkAllScalarFunction flinkAllScalarFunction) {
        flinkAllScalarFunction.setResultType(TypeExtractor.createTypeInfo(streamScalarFunction,
            StreamScalarFunction.class, streamScalarFunction.getClass(), 0));
    }

    private void initAggregateFuntion(StreamAggregateFunction<?, ?, ?> streamAggregateFunction,
        FlinkAllAggregateFunction aggregateFunction) {
        aggregateFunction.setProducedType(TypeExtractor.createTypeInfo(streamAggregateFunction,
            StreamAggregateFunction.class, streamAggregateFunction.getClass(), 0));
        aggregateFunction.setAccumulatorType(TypeExtractor.createTypeInfo(streamAggregateFunction,
            StreamAggregateFunction.class, streamAggregateFunction.getClass(), 1));
        aggregateFunction.setResultType(TypeExtractor.createTypeInfo(streamAggregateFunction,
            StreamAggregateFunction.class, streamAggregateFunction.getClass(), 2));
        aggregateFunction.setAccumulator(streamAggregateFunction.createAccumulator());
    }

    private void initTableFunction(StreamTableFunction<?> streamTableFunction, FlinkAllTableFunction tableFunction) {
        TypeInformation typeInformation = TypeExtractor.createTypeInfo(streamTableFunction, StreamTableFunction.class,
            streamTableFunction.getClass(), 0);
        tableFunction.setProducedType(typeInformation);
        tableFunction.setResultType(typeInformation);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
