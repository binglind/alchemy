package com.dfire.platform.alchemy.connectors.mysql.side;

import java.util.List;

import com.dfire.platform.alchemy.connectors.common.side.AbstractAsyncSideFunction;
import com.dfire.platform.alchemy.connectors.common.side.SideTable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author congbai
 * @date 2019/5/22
 */
public class MysqlAsyncSideFunction extends AbstractAsyncSideFunction<List<JsonObject>> {

    private static final Logger LOG = LoggerFactory.getLogger(MysqlAsyncSideFunction.class);

    private final MysqlSideProperties mysqlProperties;

    private transient MysqlSideFunction mysqlSideFunction;

    public MysqlAsyncSideFunction(SideTable sideTable, MysqlSideProperties mysqlProperties) {
        super(sideTable);
        this.mysqlProperties = mysqlProperties;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.mysqlSideFunction = new MysqlSideFunction(this.sideTable, this.mysqlProperties);
    }

    @Override
    public void timeout(Row input, ResultFuture<Row> resultFuture) throws Exception {
        if (this.sideTable.getSide().isLogTimeoutOnly()) {
            LOG.error("async request timeout from mysql");
        } else {
            super.timeout(input, resultFuture);
        }
    }

    @Override
    public void asyncInvoke(Row input, ResultFuture<Row> resultFuture) throws Exception {
        this.mysqlSideFunction.asyncInvoke(input, resultFuture);
    }

    @Override
    public List<Row> fillRecord(Row input, List<JsonObject> value) {
        return this.mysqlSideFunction.fillRecord(input, value);
    }
}
