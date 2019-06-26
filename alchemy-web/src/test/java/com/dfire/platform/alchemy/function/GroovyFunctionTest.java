package com.dfire.platform.alchemy.function;

import com.dfire.platform.alchemy.api.function.StreamScalarFunction;
import com.dfire.platform.alchemy.api.function.scalar.FlinkAllScalarFunction;
import com.dfire.platform.alchemy.api.util.GroovyCompiler;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class GroovyFunctionTest {


    String code = "import com.dfire.platform.alchemy.api.function.StreamScalarFunction;\n" +
        "\n" +
        "import java.util.Map;\n" +
        "\n" +
        "public class FindAppKey implements StreamScalarFunction<String> {\n" +
        "  \n" +
        "    @Override\n" +
        "    public String invoke(Object... args) {\n" +
        "        Object arg = args[0];\n" +
        "        if(arg == null){\n" +
        "            return null;\n" +
        "        }\n" +
        "        Map<String, String> map = (Map<String, String>) arg;\n" +
        "        Object value =  map.get(\"\\$appName\");\n" +
        "        return value == null ? null : value.toString();\n" +
        "    }\n" +
        "}";

    @Test
    public void invoke() throws Exception {
       Class clazz =  GroovyCompiler.compile(code, "FIND_APP_KEY");
        StreamScalarFunction function = (StreamScalarFunction) clazz.newInstance();
        FlinkAllScalarFunction scalarFunction = new FlinkAllScalarFunction(code, "FIND_APP_KEY");
        initScalarFuntion(function, scalarFunction);
        scalarFunction.open(null);
        Map<String, String> param = new HashMap<>();
        param.put("$appName", "test");
        assert scalarFunction.eval(param).equals("test");
    }

    private void initScalarFuntion(StreamScalarFunction<?> streamScalarFunction,
                                   FlinkAllScalarFunction flinkAllScalarFunction) {
        flinkAllScalarFunction.setResultType(TypeExtractor.createTypeInfo(streamScalarFunction,
            StreamScalarFunction.class, streamScalarFunction.getClass(), 0));
    }


}
