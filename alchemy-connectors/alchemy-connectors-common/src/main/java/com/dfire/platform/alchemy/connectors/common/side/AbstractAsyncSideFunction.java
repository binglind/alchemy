package com.dfire.platform.alchemy.connectors.common.side;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.types.Row;

import static org.apache.flink.util.Preconditions.checkArgument;

/**
 * @author congbai
 * @date 2019/5/21
 */
public abstract class AbstractAsyncSideFunction<T>  extends RichAsyncFunction<Row, Row> implements ISideFunction<T> {

    protected final SideTable sideTable;


    protected AbstractAsyncSideFunction(SideTable sideTable) {
        checkArgument(sideTable != null && sideTable.getSide() != null , "side can't be null");
        this.sideTable = sideTable;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }
}
