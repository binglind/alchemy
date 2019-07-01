package com.dfire.platform.alchemy.function;

import com.dfire.platform.alchemy.api.function.StreamAggregateFunction;
import com.dfire.platform.alchemy.api.function.StreamScalarFunction;
import com.dfire.platform.alchemy.api.function.StreamTableFunction;
import com.dfire.platform.alchemy.api.function.aggregate.FlinkAllAggregateFunction;
import com.dfire.platform.alchemy.api.function.scalar.FlinkAllScalarFunction;
import com.dfire.platform.alchemy.api.function.table.FlinkAllTableFunction;
import com.dfire.platform.alchemy.api.util.GroovyCompiler;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GroovyFunctionTest {


    String scalarCode = "import com.dfire.platform.alchemy.api.function.StreamScalarFunction;\n" +
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

    String arrgeCode = "import com.dfire.platform.alchemy.api.function.StreamAggregateFunction\n" +
        "\n" +
        "public class Count implements StreamAggregateFunction<String, Map<String, Integer>, Map<String, Integer>> {\n" +
        "\n" +
        "\n" +
        "    @Override\n" +
        "    Map<String, Integer> createAccumulator() {\n" +
        "        return new HashMap<String, Integer>();\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    void accumulate(Map<String, Integer> accumulator, String value) {\n" +
        "        Integer count = accumulator.get(value);\n" +
        "        if(count == null){\n" +
        "            accumulator.put(value, 1);\n" +
        "        }else{\n" +
        "            accumulator.put(value, count++);\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    Map<String, Integer> getValue(Map<String, Integer> accumulator) {\n" +
        "        return accumulator\n" +
        "    }\n" +
        "}\n";


    String tableCode ="import com.dfire.platform.alchemy.api.function.StreamTableFunction;\n" +
        "\n" +
        "public class Split extends StreamTableFunction<String> {\n" +
        "    @Override\n" +
        "    public void invoke(String... args) {\n" +
        "        for(String arg : args){\n" +
        "            String[] values = arg.split(\",\");\n" +
        "            for(String value : values){\n" +
        "                this.collect(value);\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}";


    @Test
    public void scalar() throws Exception {
       Class clazz =  GroovyCompiler.compile(scalarCode, "FIND_APP_KEY");
        StreamScalarFunction function = (StreamScalarFunction) clazz.newInstance();
        FlinkAllScalarFunction scalarFunction = new FlinkAllScalarFunction(scalarCode, "FIND_APP_KEY");
        initScalarFuntion(function, scalarFunction);
        scalarFunction.open(null);
        Map<String, String> param = new HashMap<>();
        param.put("$appName", "test");
        assertThat(scalarFunction.eval(param)).isEqualTo("test");
    }

    @Test
    public void arrge() throws Exception {
        Class clazz =  GroovyCompiler.compile(arrgeCode, "COUNT");
        StreamAggregateFunction function = (StreamAggregateFunction) clazz.newInstance();
        FlinkAllAggregateFunction allAggregateFunction = new FlinkAllAggregateFunction(arrgeCode, "COUNT");
        initAggregateFuntion(function, allAggregateFunction);
        allAggregateFunction.open(null);
        Map<String, Integer> acc = (Map<String, Integer>) allAggregateFunction.createAccumulator();
        allAggregateFunction.accumulate(acc, "zhangsan");
        allAggregateFunction.accumulate(acc, "zhangsan");
        allAggregateFunction.accumulate(acc, "lisi");
        allAggregateFunction.accumulate(acc, "wangwu");
        allAggregateFunction.accumulate(acc, "wangwu");
        Map<String, Integer> result = (Map<String, Integer>) allAggregateFunction.getValue(acc);
        assertThat(result.get("zhangsan")).isEqualTo("2");
        assertThat(result.get("lisi")).isEqualTo("1");
        assertThat(result.get("wangwu")).isEqualTo("2");
    }


    @Test
    public void table() throws Exception {
        Class clazz =  GroovyCompiler.compile(tableCode, "SPLIT");
        StreamTableFunction function = (StreamTableFunction) clazz.newInstance();
        FlinkAllTableFunction tableFunction = new FlinkAllTableFunction(tableCode, "SPLIT");
        initTableFunction(function, tableFunction);
        tableFunction.open(null);
        tableFunction.eval("one,two,three,four");
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


}
