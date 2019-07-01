package com.dfire.platform.alchemy.function

import com.dfire.platform.alchemy.api.function.StreamAggregateFunction

public class Count implements StreamAggregateFunction<String, Map<String, Integer>, Map<String, Integer>> {


    @Override
    Map<String, Integer> createAccumulator() {
        return new HashMap<String, Integer>();
    }

    @Override
    void accumulate(Map<String, Integer> accumulator, String value) {
        Integer count = accumulator.get(value);
        if(count == null){
            accumulator.put(value, 1);
        }else{
            accumulator.put(value, count++);
        }
    }

    @Override
    Map<String, Integer> getValue(Map<String, Integer> accumulator) {
        return accumulator
    }
}
