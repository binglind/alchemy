import com.dfire.platform.alchemy.api.function.StreamScalarFunction

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
