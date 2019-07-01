package com.dfire.platform.alchemy.function;

import com.dfire.platform.alchemy.api.function.StreamTableFunction;

public class Split extends StreamTableFunction<String> {
    @Override
    public void invoke(String... args) {
        for(String arg : args){
            String[] values = arg.split(",");
            for(String value : values){
                this.collect(value);
            }
        }
    }
}
