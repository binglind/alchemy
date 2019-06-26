package com.dfire.platform.alchemy.connectors.mysql.side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.dfire.platform.alchemy.connectors.common.Cache;
import com.dfire.platform.alchemy.connectors.common.Future;
import com.dfire.platform.alchemy.connectors.common.side.ISideFunction;
import com.dfire.platform.alchemy.connectors.common.side.SideTable;
import com.dfire.platform.alchemy.api.util.SideParser;
import com.dfire.platform.alchemy.api.util.ConvertObjectUtil;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.InferTypes;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.calcite.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dfire.platform.alchemy.api.common.Alias;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * @author congbai
 * @date 2019/5/27
 */
public class MysqlSideFunction implements ISideFunction<List<JsonObject>> {

    private static final Logger LOG = LoggerFactory.getLogger(MysqlAsyncSideFunction.class);

    private final static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    public final static int DEFAULT_VERTX_EVENT_LOOP_POOL_SIZE = 1;

    public final static int DEFAULT_VERTX_WORKER_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    public final static int DEFAULT_MAX_DB_CONN_POOL_SIZE
        = DEFAULT_VERTX_EVENT_LOOP_POOL_SIZE + DEFAULT_VERTX_WORKER_POOL_SIZE;

    public final static String KEY_SEP = "_";

    private final SideTable sideTableInfo;

    private final String sql;

    private final Vertx vertx;

    private final SQLClient sqlClient;

    private final Cache<List<JsonObject>> cache;

    public MysqlSideFunction(SideTable sideTableInfo, MysqlSideProperties mysqlProperties) throws SqlParseException {
        this.sideTableInfo = sideTableInfo;
        this.sql = modifySql(sideTableInfo);
        this.vertx = createVertx();
        this.sqlClient = createClient(mysqlProperties, vertx);
        this.cache = create(this.sideTableInfo.getSide());
    }

    private String modifySql(SideTable sideTable) throws SqlParseException {
        SqlParser.Config config = SqlParser.configBuilder().setLex(Lex.MYSQL).build();
        SqlParser sqlParser = SqlParser.create(sideTable.getSql(), config);
        SqlNode sqlNode = sqlParser.parseStmt();
        if (SqlKind.SELECT != sqlNode.getKind()) {
            throw new UnsupportedOperationException(
                "MysqlAsyncReqRow only support query sql, sql:" + sideTable.getSql());
        }
        SqlSelect sqlSelect = (SqlSelect)sqlNode;
        SqlNode whereNode = sqlSelect.getWhere();
        SqlBinaryOperator and = new SqlBinaryOperator("AND", SqlKind.AND, 24, true,
            ReturnTypes.BOOLEAN_NULLABLE_OPTIMIZED, InferTypes.BOOLEAN, OperandTypes.BOOLEAN_BOOLEAN);
        List<SqlBasicCall> conditionNodes = createConditionNodes(sideTable.getConditions(), sideTable.getSideAlias());
        List<SqlNode> nodes = new ArrayList<>();
        nodes.addAll(conditionNodes);
        if (whereNode != null) {
            nodes.add(whereNode);
        } else {
            SqlBinaryOperator equal = new SqlBinaryOperator("=", SqlKind.EQUALS, 30, true, ReturnTypes.BOOLEAN_NULLABLE,
                InferTypes.FIRST_KNOWN, OperandTypes.COMPARABLE_UNORDERED_COMPARABLE_UNORDERED);
            SqlBasicCall andEqual
                = new SqlBasicCall(equal, SideParser.createEqualNodes(SqlKind.AND), new SqlParserPos(0, 0));
            nodes.add(andEqual);
        }
        SqlBasicCall sqlBasicCall
            = new SqlBasicCall(and, nodes.toArray(new SqlNode[nodes.size()]), new SqlParserPos(0, 0));
        sqlSelect.setWhere(sqlBasicCall);
        return sqlSelect.toString();
    }

    private List<SqlBasicCall> createConditionNodes(List<String> conditions, Alias alias) {
        SqlBinaryOperator equal = new SqlBinaryOperator("=", SqlKind.EQUALS, 30, true, ReturnTypes.BOOLEAN_NULLABLE,
            InferTypes.FIRST_KNOWN, OperandTypes.COMPARABLE_UNORDERED_COMPARABLE_UNORDERED);
        List<SqlBasicCall> nodes = new ArrayList<>(conditions.size());
        int num = 0;
        for (String condition : conditions) {
            List<String> fields = new ArrayList<>(2);
            fields.add(alias.getAlias());
            fields.add(condition);
            SqlIdentifier leftIdentifier = new SqlIdentifier(fields, new SqlParserPos(0, 0));
            SqlDynamicParam sqlDynamicParam = new SqlDynamicParam(num++, new SqlParserPos(0, 0));
            SqlNode[] sqlNodes = new SqlNode[2];
            sqlNodes[0] = leftIdentifier;
            sqlNodes[1] = sqlDynamicParam;
            SqlBasicCall andEqual = new SqlBasicCall(equal, sqlNodes, new SqlParserPos(0, 0));
            nodes.add(andEqual);
        }
        return nodes;
    }

    private SQLClient createClient(MysqlSideProperties mysqlProperties, Vertx vertx) {
        JsonObject mysqlClientConfig = new JsonObject();
        mysqlClientConfig.put("url", mysqlProperties.getUrl()).put("driver_class", MYSQL_DRIVER)
            .put("max_pool_size",
                mysqlProperties.getMaxPoolSize() == null ? DEFAULT_MAX_DB_CONN_POOL_SIZE
                    : mysqlProperties.getMaxPoolSize())
            .put("user", mysqlProperties.getUsername()).put("password", mysqlProperties.getPassword());

        return JDBCClient.createNonShared(vertx, mysqlClientConfig);
    }

    private Vertx createVertx() {
        VertxOptions vo = new VertxOptions();
        vo.setEventLoopPoolSize(DEFAULT_VERTX_EVENT_LOOP_POOL_SIZE);
        vo.setWorkerPoolSize(DEFAULT_VERTX_WORKER_POOL_SIZE);
        return Vertx.vertx(vo);
    }

    public void invoke(Row input, Collector<Row> out) throws Exception {
        CompletableFuture<List<Row>> completableFuture = new CompletableFuture();
        Future<CompletableFuture> future = new Future<>(completableFuture);
        asyncInvoke(input, future);
        try {
            List<Row> rows = future.get();
            if (rows == null) {
                return;
            }
            for (Row row : rows) {
                out.collect(row);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void asyncInvoke(Row input, ResultFuture<Row> resultFuture) throws Exception {
        asyncInvoke(input, new Future<>(resultFuture));
    }

    public <T> void asyncInvoke(Row input, Future<T> future) throws Exception {
        JsonArray params = createParams(input, this.sideTableInfo.getConditionIndexs());
        if (params == null) {
            List<Row> rows = dealMissKey(input, this.sideTableInfo.getJoinType());
            future.complete(rows);
            return;
        }
        String cacheKey = buildCacheKey(params);
        // query from cache
        Optional<List<JsonObject>> optionalValue = getOrNull(this.cache, cacheKey);
        if (optionalValue != null) {
            sendResult(input, future, optionalValue);
            return;
        }
        // query from db
        sqlClient.getConnection(conn -> {
            if (conn.failed()) {
                // Treatment failures
                future.completeExceptionally(conn.cause());
                return;
            }
            final SQLConnection connection = conn.result();
            connection.queryWithParams(sql, params, rs -> {
                if (rs.failed()) {
                    LOG.error("Cannot retrieve the data from the database", rs.cause());
                    future.complete(null);
                    return;
                }
                int resultSize = rs.result().getResults().size();
                Optional<List<JsonObject>> results;
                if (resultSize > 0) {
                    List<JsonObject> cacheContent = rs.result().getRows();
                    results = Optional.of(cacheContent);
                } else {
                    results = Optional.empty();
                }
                setOrNull(this.cache, cacheKey, results);
                sendResult(input, future, results);
                // and close the connection
                connection.close(done -> {
                    if (done.failed()) {
                        throw new RuntimeException(done.cause());
                    }
                });
            });
        });
    }

    private void sendResult(Row input, Future future, Optional<List<JsonObject>> optionalValue) {
        if (!optionalValue.isPresent()) {
            future.complete(dealMissKey(input, this.sideTableInfo.getJoinType()));
        } else {
            future.complete(fillRecord(input, optionalValue.get()));
        }
    }

    private String buildCacheKey(JsonArray params) {
        StringBuilder sb = new StringBuilder();
        for (Object value : params.getList()) {
            sb.append(String.valueOf(value)).append(KEY_SEP);
        }
        return sb.toString();
    }

    private JsonArray createParams(Row input, List<Integer> conditionIndexs) {
        JsonArray params = new JsonArray();
        for (Integer index : conditionIndexs) {
            Object param = input.getField(index);
            if (param == null) {
                LOG.warn("join condition is null ,index:{}", index);
                return null;
            }
            params.add(param);
        }
        return params;
    }

    @Override
    public List<Row> fillRecord(Row input, List<JsonObject> jsonArrays) {
        List<Row> rowList = Lists.newArrayList();
        for (JsonObject value : jsonArrays) {
            Row row = fillRecord(input, value);
            rowList.add(row);
        }
        return rowList;
    }

    @Override
    public void close()  throws Exception{
        sqlClient.close();
    }

    private Row fillRecord(Row input, JsonObject value) {
        RowTypeInfo sideTable = this.sideTableInfo.getSideType();
        int sideSize = sideTable.getArity();
        int inputSize = input.getArity();
        if (this.sideTableInfo.getRowSize() != (sideSize + inputSize)) {
            LOG.warn("expected row size:{} ,Row:{} , side:{}", this.sideTableInfo.getRowSize(), input, value);
            throw new IllegalArgumentException("expected row size:" + this.sideTableInfo.getRowSize()
                + ", but input size:" + inputSize + " and side size:" + sideSize);
        }
        Row row = new Row(this.sideTableInfo.getRowSize());
        for (int i = 0; i < inputSize; i++) {
            row.setField(i, input.getField(i));
        }
        RowTypeInfo sideType = this.sideTableInfo.getSideType();
        Map<Integer, String> indexFields = getIndexFields(sideType);
        for (int i = 0; i < sideSize; i++) {
            Object result = value.getValue(indexFields.get(i));
            row.setField(i + inputSize, ConvertObjectUtil.transform(result, sideType.getTypeAt(i)));
        }
        return row;
    }

    private Map<Integer, String> getIndexFields(RowTypeInfo sideType) {
        Map<Integer, String> indexFields = new HashMap<>(sideType.getArity());
        String[] fieldNames = sideType.getFieldNames();
        for (String field : fieldNames) {
            indexFields.put(sideType.getFieldIndex(field), field);
        }
        return indexFields;
    }

}
