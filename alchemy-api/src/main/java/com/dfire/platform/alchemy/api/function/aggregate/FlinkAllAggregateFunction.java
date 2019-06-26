package com.dfire.platform.alchemy.api.function.aggregate;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.util.Preconditions;

import com.dfire.platform.alchemy.api.function.StreamAggregateFunction;
import com.dfire.platform.alchemy.api.util.GroovyCompiler;

/**
 * @author congbai
 * @date 31/05/2018
 */
public class FlinkAllAggregateFunction extends AggregateFunction implements ResultTypeQueryable {

    private final String code;

    private final String name;

    private Object accumulator;

    private StreamAggregateFunction streamAggregateFunction;

    private TypeInformation accumulatorType;

    private TypeInformation resultType;

    private TypeInformation producedType;

    public FlinkAllAggregateFunction(String code, String name) {
        this.code = Preconditions.checkNotNull(code, "code must not be null.");
        this.name = Preconditions.checkNotNull(name, "name must not be null.");
    }

    public FlinkAllAggregateFunction(StreamAggregateFunction streamAggregateFunction) {
        this.streamAggregateFunction
            = Preconditions.checkNotNull(streamAggregateFunction, "function must not be null.");
        this.code = null;
        this.name = null;
    }

    @Override
    public Object createAccumulator() {
        if (this.streamAggregateFunction == null) {
            return accumulator;
        } else {
            return this.streamAggregateFunction.createAccumulator();
        }
    }

    public void accumulate(Object accumulator, Object value) {
        final StreamAggregateFunction aggregateFunction = getStreamAggregateFunction();
        aggregateFunction.accumulate(accumulator, value);
    }

    @Override
    public Object getValue(Object accumulator) {
        final StreamAggregateFunction aggregateFunction = getStreamAggregateFunction();
        return aggregateFunction.getValue(accumulator);
    }

    @Override
    public TypeInformation getAccumulatorType() {
        return accumulatorType;
    }

    public void setAccumulatorType(TypeInformation accumulatorType) {
        this.accumulatorType = accumulatorType;
    }

    @Override
    public TypeInformation getResultType() {
        return resultType;
    }

    public void setResultType(TypeInformation resultType) {
        this.resultType = resultType;
    }

    @Override
    public TypeInformation getProducedType() {
        return producedType;
    }

    public void setProducedType(TypeInformation producedType) {
        this.producedType = producedType;
    }

    public void setAccumulator(Object accumulator) {
        this.accumulator = accumulator;
    }

    public StreamAggregateFunction getStreamAggregateFunction() {
        if (this.streamAggregateFunction == null && StringUtils.isNotEmpty(this.code)) {
            Class<StreamAggregateFunction> clazz = GroovyCompiler.compile(code, name);
            try {
                this.streamAggregateFunction = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.streamAggregateFunction;
    }

}
