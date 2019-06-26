# udf.md

支持页面写udf和jar包加载udf
![](/docs/media/15615340764242/15615342185239.jpg)

* 输入函数的名称。sql任务中使用的是这个名称。
* 选择类型，两种，DEPENDENCY和CODE
* 如果是DEPENDENCY，在值中填入具体函数类的全路径，如：com.dfire.platform.alchemy.function.logstash.GrokFunction；如果是CODE，在值中填入java代码，必须实现StreamAggregateFunction、StreamScalarFunction、StreamTableFunction中的一个接口，比如：

```java
import com.dfire.platform.alchemy.api.function.StreamScalarFunction;

import java.util.Map;

public class FindAppKey implements StreamScalarFunction<String> {

    @Override
    public String invoke(Object... args) {
        Object arg = args[0];
        if(arg == null){
            return null;
        }
        Map<String, String> map = (Map<String, String>) arg;
        Object value =  map.get("\$appName");
        return value == null ? null : value.toString();
    }
}

```

* 如果是DEPENDENCY，在依赖中填入maven依赖，如com.dfire.function:logstash:0.0.1-SNAPSHOT。




