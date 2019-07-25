package com.dfire.platform.alchemy.connectors.common.util;

import com.dfire.platform.alchemy.connectors.common.elasticsearch.FailureHandler;
import com.dfire.platform.alchemy.connectors.common.elasticsearch.IgnoreFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.util.NoOpFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.util.RetryRejectedExecutionFailureHandler;

public class ActionRequestFailureHandlerUtil {

    public static ActionRequestFailureHandler createFailureHandler(String failureHandler) {
        if (failureHandler == null || failureHandler.trim().length() == 0) {
            return new NoOpFailureHandler();
        }
        FailureHandler handler = FailureHandler.valueOf(failureHandler.toUpperCase());
        switch (handler) {
            case IGNORE:
                return new IgnoreFailureHandler();
            case RETRYREJECTED:
                return new RetryRejectedExecutionFailureHandler();
            default:
                return new NoOpFailureHandler();
        }
    }

}
