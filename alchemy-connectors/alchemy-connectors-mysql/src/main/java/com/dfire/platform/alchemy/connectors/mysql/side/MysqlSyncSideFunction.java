package com.dfire.platform.alchemy.connectors.mysql.side;

import java.util.List;

import com.dfire.platform.alchemy.connectors.common.side.SideTable;
import com.dfire.platform.alchemy.connectors.common.side.AbstractSyncSideFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author congbai
 * @date 2019/5/21
 */
public class MysqlSyncSideFunction extends AbstractSyncSideFunction<List<JsonObject>> {

    private static final Logger LOG = LoggerFactory.getLogger(MysqlAsyncSideFunction.class);

    private final MysqlSideProperties mysqlProperties;

    private transient MysqlSideFunction mysqlSideFunction;

    public MysqlSyncSideFunction(SideTable sideTable, MysqlSideProperties mysqlProperties) {
        super(sideTable);
        this.mysqlProperties = mysqlProperties;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.mysqlSideFunction = new MysqlSideFunction(this.sideTable, this.mysqlProperties);
    }

    @Override
    public void flatMap(Row value, Collector<Row> out) throws Exception {
        this.mysqlSideFunction.invoke(value, out);
    }

    @Override
    public List<Row> fillRecord(Row input, List<JsonObject> value) {
        return this.mysqlSideFunction.fillRecord(input, value);
    }
}
