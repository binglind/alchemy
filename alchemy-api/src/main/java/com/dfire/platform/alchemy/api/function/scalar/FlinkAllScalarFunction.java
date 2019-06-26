package com.dfire.platform.alchemy.api.function.scalar;

import com.dfire.platform.alchemy.api.function.StreamScalarFunction;
import com.dfire.platform.alchemy.api.util.GroovyCompiler;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.util.Preconditions;

/**
 * @author congbai
 * @date 31/05/2018
 */
public class FlinkAllScalarFunction extends ScalarFunction {

    private final String code;

    private final String name;

    private TypeInformation resultType;

    private StreamScalarFunction streamScalarFunction;

    public FlinkAllScalarFunction(String code, String name) {
        this.code = Preconditions.checkNotNull(code, "code must not be null.");
        this.name = Preconditions.checkNotNull(name, "name must not be null.");
    }

    public FlinkAllScalarFunction(StreamScalarFunction streamScalarFunction) {
        this.streamScalarFunction = Preconditions.checkNotNull(streamScalarFunction, "function must not be null.");
        this.code = null;
        this.name = null;
    }

    @Override
    public void open(FunctionContext context) throws Exception {
        if (streamScalarFunction == null) {
            Class<StreamScalarFunction> clazz = GroovyCompiler.compile(code, name);
            this.streamScalarFunction = clazz.newInstance();
        }
        super.open(context);
    }

    public Object eval(Object... args) {
        return this.streamScalarFunction.invoke(args);
    }


    public void setResultType(TypeInformation resultType) {
        this.resultType = resultType;
    }

}
