package com.dfire.platform.alchemy.service.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

import com.google.common.collect.Lists;

/**
 * @author congbai
 * @date 2019/6/3
 */
public class SqlParseUtil {

    private static final SqlParser.Config CONFIG = SqlParser.configBuilder().setLex(Lex.MYSQL).build();

    public static void parse(List<String> sqls, List<String> sources, List<String> udfs, List<String> sinks)
        throws SqlParseException {
        for (String sql : sqls) {
            SqlParser sqlParser = SqlParser.create(sql, CONFIG);
            SqlNode sqlNode = sqlParser.parseStmt();
            SqlKind kind = sqlNode.getKind();
            switch (kind){
                case INSERT:
                    SqlInsert sqlInsert = (SqlInsert)sqlNode;
                    addSink(sinks, findSinkName(sqlInsert));
                    SqlSelect source = (SqlSelect) sqlInsert.getSource();
                    parseSource(source, sources, udfs);
                    break;
                case SELECT:
                    parseSource((SqlSelect) sqlNode, sources, udfs);
                    break;
                default:
                    throw new IllegalArgumentException("It must be an insert SQL, sql:" + sql);
            }
        }
    }

    public static List<String> findQuerySql(List<String> sqls)
        throws SqlParseException {
        List<String> newSqls = new ArrayList<>(sqls.size());
        for (String sql : sqls) {
            SqlParser sqlParser = SqlParser.create(sql, CONFIG);
            SqlNode sqlNode = sqlParser.parseStmt();
            if (sqlNode.getKind() != SqlKind.INSERT) {
                throw new IllegalArgumentException("It must be an insert SQL, sql:" + sql);
            }
            SqlInsert sqlInsert = (SqlInsert)sqlNode;
            newSqls.add(sqlInsert.getSource().toString());
        }
        return newSqls;
    }

    private static void addSink(List<String> newSinks, String sinkName) {
        if (!newSinks.contains(sinkName)) {
            newSinks.add(sinkName);
        }
    }

    private static void addUdf(List<String> newUdfs, String udfName) {
        if (!newUdfs.contains(udfName)) {
            newUdfs.add(udfName);
        }
    }

    private static void addSource(List<String> newSources, String sourceName) {
        if (!newSources.contains(sourceName)) {
            newSources.add(sourceName);
        }
    }

    private static void parseSource(SqlSelect sqlSelect, List<String> sources, List<String> udfs)
        throws SqlParseException {
        SqlNodeList selectList = sqlSelect.getSelectList();
        SqlNode from = sqlSelect.getFrom();
        SqlNode where = sqlSelect.getWhere();
        SqlNode having = sqlSelect.getHaving();
        parseSelectList(selectList, sources, udfs);
        parseFrom(from, sources, udfs);
        parseFunction(where, udfs);
        parseFunction(having, udfs);
    }

    /**
     * 解析select 字段中的函数
     * 
     * @param sqlNodeList
     * @param udfs
     */
    private static void parseSelectList(SqlNodeList sqlNodeList, List<String> sources, List<String> udfs)
        throws SqlParseException {
        for (SqlNode sqlNode : sqlNodeList) {
            parseSelect(sqlNode, sources, udfs);
        }
    }

    private static void parseFrom(SqlNode from, List<String> sources, List<String> udfs) throws SqlParseException {
        SqlKind sqlKind = from.getKind();
        switch (sqlKind) {
            case IDENTIFIER:
                SqlIdentifier identifier = (SqlIdentifier)from;
                addSource(sources, identifier.getSimple());
                break;
            case AS:
                SqlBasicCall sqlBasicCall = (SqlBasicCall)from;
                parseFrom(sqlBasicCall.operand(0), sources, udfs);
                break;
            case SELECT:
                parseSource((SqlSelect)from, sources, udfs);
                break;
            case JOIN:
                SqlJoin sqlJoin = (SqlJoin)from;
                SqlNode left = sqlJoin.getLeft();
                SqlNode right = sqlJoin.getRight();
                parseFrom(left, sources, udfs);
                parseFrom(right, sources, udfs);
                break;
            default:
        }
    }

    private static void parseFunction(SqlNode sqlNode, List<String> udfs) {
        if (sqlNode instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall)sqlNode;
            SqlOperator operator = sqlBasicCall.getOperator();
            if (operator instanceof SqlFunction) {
                SqlFunction sqlFunction = (SqlFunction)operator;
                SqlFunctionCategory category = sqlFunction.getFunctionType();
                switch (category) {
                    case USER_DEFINED_FUNCTION:
                    case USER_DEFINED_SPECIFIC_FUNCTION:
                    case USER_DEFINED_TABLE_FUNCTION:
                    case USER_DEFINED_TABLE_SPECIFIC_FUNCTION:
                        addUdf(udfs, sqlFunction.getName());
                        break;
                    default:
                }
            } else {
                parseFunction(sqlBasicCall.operand(0), udfs);
            }
        }
    }

    private static void parseSelect(SqlNode sqlNode, List<String> sources, List<String> udfs) throws SqlParseException {
        SqlKind sqlKind = sqlNode.getKind();
        switch (sqlKind) {
            case IDENTIFIER:
                break;
            case AS:
                SqlNode firstNode = ((SqlBasicCall)sqlNode).operand(0);
                parseSelect(firstNode, sources, udfs);
                break;
            case SELECT:
                parseSource((SqlSelect)sqlNode, sources, udfs);
                break;
            default:
                parseFunction(sqlNode, udfs);

        }
    }

    public static String parseSinkName(String sql) throws SqlParseException {
        SqlParser sqlParser = SqlParser.create(sql, CONFIG);
        SqlNode sqlNode = sqlParser.parseStmt();
        SqlKind sqlKind = sqlNode.getKind();
        if (sqlKind != SqlKind.INSERT) {
            throw new IllegalArgumentException("It must be an insert SQL, sql:" + sql);
        }
        return findSinkName((SqlInsert)sqlNode);
    }

    private static String findSinkName(SqlInsert sqlInsert) {
        SqlNode target = sqlInsert.getTargetTable();
        SqlKind targetKind = target.getKind();
        if (targetKind != SqlKind.IDENTIFIER) {
            throw new IllegalArgumentException("invalid insert SQL, sql:" + sqlInsert.toString());
        }
        SqlIdentifier identifier = (SqlIdentifier)target;
        return identifier.getSimple();
    }
}
