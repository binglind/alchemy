package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.print.PrintTableSink;

/**
 * @author congbai
 * @date 2019/5/24
 */
public class PrintSinkDescriptor extends SinkDescriptor {
    @Override
    public String getName() {
        return "print_sink";
    }

    @Override
    public <T> T transform() throws Exception {
        return (T)new PrintTableSink();
    }

    @Override
    public String type() {
        return Constants.SINK_TYPE_VALUE_PRINT;
    }

    @Override
    public void validate() throws Exception {
        // nothing to do
    }
}
