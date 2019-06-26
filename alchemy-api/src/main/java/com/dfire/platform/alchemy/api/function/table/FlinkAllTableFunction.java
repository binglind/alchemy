package com.dfire.platform.alchemy.api.function.table;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.util.Preconditions;

import com.dfire.platform.alchemy.api.function.StreamTableFunction;
import com.dfire.platform.alchemy.api.util.GroovyCompiler;

/**
 * @author congbai
 * @date 31/05/2018
 */
public class FlinkAllTableFunction extends TableFunction implements ResultTypeQueryable, Collect {

    private final String code;

    private final String name;

    private StreamTableFunction streamTableFunction;

    private TypeInformation producedType;

    private TypeInformation resultType;

    public FlinkAllTableFunction(String code, String name) {
        this.code = Preconditions.checkNotNull(code, "code must not be null.");
        this.name = Preconditions.checkNotNull(name, "name must not be null.");
    }

    public FlinkAllTableFunction(StreamTableFunction streamTableFunction) {
        this.streamTableFunction = Preconditions.checkNotNull(streamTableFunction, "function must not be null.");
        this.name = null;
        this.code = null;
    }

    @Override
    public void open(FunctionContext context) throws Exception {
        if (streamTableFunction == null) {
            Class<StreamTableFunction> clazz = GroovyCompiler.compile(code, name);
            this.streamTableFunction = clazz.newInstance();
        }
        this.streamTableFunction.setCollect(this);
        super.open(context);
    }

    public void eval(Object... args) {
        this.streamTableFunction.invoke(args);
    }

    @Override
    public TypeInformation getProducedType() {
        return producedType;
    }

    public void setProducedType(TypeInformation producedType) {
        this.producedType = producedType;
    }

    @Override
    public TypeInformation getResultType() {
        return resultType;
    }

    public void setResultType(TypeInformation resultType) {
        this.resultType = resultType;
    }
}
