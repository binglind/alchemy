package com.dfire.platform.alchemy.api.util;


import com.dfire.platform.alchemy.api.common.Alias;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlAsOperator;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.InferTypes;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;


import org.apache.commons.lang3.StringUtils;
import org.apache.flink.calcite.shaded.com.google.common.collect.ImmutableList;
import org.apache.flink.calcite.shaded.com.google.common.collect.Lists;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * @author congbai
 * @date 2019/5/21
 */
public class SideParser {

    private static final SqlParser.Config CONFIG = SqlParser.configBuilder().setLex(Lex.MYSQL).build();

    public static Deque<SqlNode> parse(String sql) throws SqlParseException {
        SqlParser sqlParser = SqlParser.create(sql, CONFIG);
        SqlNode sqlNode = sqlParser.parseStmt();
        Deque<SqlNode> deque = new ArrayDeque<>();
        parse(sqlNode, deque);
        return deque;
    }

    public static void parse(SqlNode sqlNode, Deque<SqlNode> deque) {
        deque.offer(sqlNode);
        SqlKind sqlKind = sqlNode.getKind();
        switch (sqlKind) {
            case INSERT:
                SqlNode sqlSource = ((SqlInsert)sqlNode).getSource();
                parse(sqlSource, deque);
                break;
            case SELECT:
                SqlNode sqlFrom = ((SqlSelect)sqlNode).getFrom();
                parse(sqlFrom, deque);
                break;
            case JOIN:
                SqlNode sqlLeft = ((SqlJoin)sqlNode).getLeft();
                SqlNode sqlRight = ((SqlJoin)sqlNode).getRight();
                parse(sqlLeft, deque);
                parse(sqlRight, deque);
                break;
            case AS:
                SqlNode sqlAs = ((SqlBasicCall)sqlNode).getOperands()[0];
                parse(sqlAs, deque);
                break;
            default:
                return;
        }
    }

    public static Alias getTableName(SqlNode sqlNode) {
        SqlKind sqlKind = sqlNode.getKind();
        Alias alias;
        switch (sqlKind) {
            case IDENTIFIER:
                SqlIdentifier sqlIdentifier = (SqlIdentifier)sqlNode;
                alias = new Alias(sqlIdentifier.names.get(0), sqlIdentifier.names.get(0));
                break;
            case AS:
                SqlBasicCall sqlBasicCall = (SqlBasicCall)sqlNode;
                SqlNode first = sqlBasicCall.getOperands()[0];
                SqlNode second = sqlBasicCall.getOperands()[1];
                if (first.getKind() == SqlKind.IDENTIFIER) {
                    alias = new Alias(((SqlIdentifier)first).names.get(0), ((SqlIdentifier)second).names.get(0));
                } else {
                    alias = new Alias(((SqlIdentifier)second).names.get(0), ((SqlIdentifier)second).names.get(0));
                }
                break;
            default:
                throw new UnsupportedOperationException("暂时不支持" + sqlKind);
        }
        return alias;
    }

    public static void rewrite(SqlNode sqlNode, SqlSelect sqlSelect) {
        SqlKind sqlKind = sqlNode.getKind();
        switch (sqlKind) {
            case INSERT:
                SqlInsert sqlInsert = ((SqlInsert)sqlNode);
                sqlInsert.setSource(sqlSelect);
                break;
            case SELECT:
                SqlSelect select = (SqlSelect)sqlNode;
                select.setFrom(sqlSelect);
                break;
            case AS:
                SqlBasicCall basicCall = (SqlBasicCall)sqlNode;
                basicCall.setOperand(0, sqlSelect);
                break;
            default:
                throw new UnsupportedOperationException(sqlKind + "目前不支持维表操作");
        }
    }

    /**
     *  select a.name , a.age , FUN(a.weight) as weight from test  -->  { name , age , weight}
     * @param selectList
     * @return
     */
    public static List<String> findSelectField(SqlNodeList selectList){
        List<SqlNode> nodes = selectList.getList();
        List<String> fields = new ArrayList<>();
        for (SqlNode node : nodes){
            SqlKind kind = node.getKind();
            String field;
            switch (kind){
                case AS:
                    SqlBasicCall call = (SqlBasicCall) node;
                    field = findField(call.operand(0));
                    break;
                case IDENTIFIER:
                    field = findField(node);
                    break;
                default:
                    throw new UnsupportedOperationException("Don't supported findSelectField in" + node);
            }
            if (StringUtils.isEmpty(field)){
                // a.*
                return Collections.emptyList();
            }else{
                fields.add(field);
            }
        }
        return fields;
    }

    private static String findField(SqlNode sqlNode){
        SqlKind kind = sqlNode.getKind();
        switch (kind){
            case IDENTIFIER:
                SqlIdentifier identifier = (SqlIdentifier) sqlNode;
                ImmutableList<String> names = identifier.names;
                if (names.size()== 1){
                   return names.get(0);
                }else if(names.size() == 2){
                    return names.get(1);
                }
            default:
                throw new UnsupportedOperationException("Don't supported findField in" + sqlNode);
        }
    }


    public static List<String> findConditionFields(SqlNode conditionNode, String specifyTableName){
        List<SqlNode> sqlNodeList = Lists.newArrayList();
        if(conditionNode.getKind() == SqlKind.AND){
            sqlNodeList.addAll(Lists.newArrayList(((SqlBasicCall)conditionNode).getOperands()));
        }else{
            sqlNodeList.add(conditionNode);
        }

        List<String> conditionFields = Lists.newArrayList();
        for(SqlNode sqlNode : sqlNodeList){
            if(sqlNode.getKind() != SqlKind.EQUALS){
                throw new RuntimeException("not equal operator.");
            }
            SqlIdentifier left = (SqlIdentifier)((SqlBasicCall)sqlNode).getOperands()[0];
            SqlIdentifier right = (SqlIdentifier)((SqlBasicCall)sqlNode).getOperands()[1];

            String leftTableName = left.names.get(0);
            String rightTableName = right.names.get(0);

            String tableCol;
            if(leftTableName.equalsIgnoreCase(specifyTableName)){
                tableCol = left.names.get(1);
            }else if(rightTableName.equalsIgnoreCase(specifyTableName)){
                tableCol = right.names.get(1);
            }else{
                throw new RuntimeException(String.format("side table:%s join condition is wrong", specifyTableName));
            }
            conditionFields.add(tableCol);
        }

        return conditionFields;
    }


    public static SqlSelect newSelect(SqlSelect selectSelf, String table, String alias, boolean left, boolean newTable) {
        List<SqlNode> operand = selectSelf.getOperandList();
        SqlNodeList keywordList = (SqlNodeList)operand.get(0);
        SqlNodeList selectList = (SqlNodeList)operand.get(1);
        SqlNode from = operand.get(2);
        SqlNode where = operand.get(3);
        SqlNodeList groupBy = (SqlNodeList)operand.get(4);
        SqlNode having = operand.get(5);
        SqlNodeList windowDecls = (SqlNodeList)operand.get(6);
        SqlNodeList orderBy = (SqlNodeList)operand.get(7);
        SqlNode offset = operand.get(8);
        SqlNode fetch = operand.get(9);
        if (left) {
            return newSelect(selectSelf.getParserPosition(), keywordList, selectList, ((SqlJoin)from).getLeft(), where,
                groupBy, having, windowDecls, orderBy, offset, fetch, alias, newTable);
        }
        if (newTable) {
            return newSelect(selectSelf.getParserPosition(), null,  creatFullNewSelectList(alias, selectList), createNewFrom(table, alias, from),
                where, groupBy, having, windowDecls, orderBy, offset, fetch, alias, newTable);
        } else {
            return newSelect(selectSelf.getParserPosition(), null, selectList, ((SqlJoin)from).getRight(), where,
                groupBy, having, windowDecls, orderBy, offset, fetch, alias, newTable);
        }

    }

    private static SqlNode createNewFrom(String table, String alias, SqlNode from) {
        SqlIdentifier identifierFirst = new SqlIdentifier(table, from.getParserPosition());
        SqlIdentifier identifierSecond = new SqlIdentifier(alias, from.getParserPosition());
        return new SqlBasicCall(new SqlAsOperator(), new SqlNode[] {identifierFirst, identifierSecond}, from.getParserPosition());
    }

    private static SqlNodeList creatFullNewSelectList(String alias, SqlNodeList selectList) {
        SqlNodeList newSelectList = new SqlNodeList( selectList.getParserPosition());
        List<String> names = new ArrayList<>(2);
        names.add(alias);
        names.add("");
        newSelectList.add(new SqlIdentifier(names,new SqlParserPos(0,0)));
        return newSelectList;
    }

    private static SqlSelect newSelect(SqlParserPos parserPosition, SqlNodeList keywordList, SqlNodeList selectList,
        SqlNode fromNode, SqlNode whereNode, SqlNodeList groupByNode, SqlNode havingNode, SqlNodeList windowDeclsList,
        SqlNodeList orderByList, SqlNode offsetNode, SqlNode fetchNode, String alias,
        boolean newTable) {
        SqlNodeList keyword = keywordList;
        SqlNodeList select = newTable ? changeTableName(selectList,alias): reduce(selectList, alias);
        SqlNode from = fromNode == null ? null : fromNode;
        SqlNode where =newTable ? changeTableName(whereNode,alias): reduce(whereNode, alias);
        SqlNodeList groupBy = newTable ? changeTableName(groupByNode,alias): reduce(groupByNode, alias);
        SqlNode having = newTable ? changeTableName(havingNode,alias): reduce(havingNode, alias);
        SqlNodeList windowDecls = newTable ? changeTableName(windowDeclsList,alias): reduce(windowDeclsList, alias);
        SqlNodeList orderBy = newTable ? changeTableName(orderByList,alias): reduce(orderByList, alias);
        SqlNode offset = newTable ? changeTableName(offsetNode,alias): reduce(offsetNode, alias);
        SqlNode fetch = newTable ? changeTableName(fetchNode,alias): reduce(fetchNode, alias);
        return new SqlSelect(parserPosition, keyword, select, from, where, groupBy, having, windowDecls, orderBy,
            offset, fetch);
    }

    private static SqlNode reduce(SqlNode sqlNode, String alias) {
        if (sqlNode == null) {
            return null;
        }
        SqlNode cloneNode = sqlNode.clone(sqlNode.getParserPosition());
        SqlKind sqlKind = cloneNode.getKind();
        switch (sqlKind) {
            case IDENTIFIER:
                SqlIdentifier sqlIdentifier = (SqlIdentifier)cloneNode;
                String tableName = sqlIdentifier.names.get(0);
                if (tableName.equalsIgnoreCase(alias)) {
                    return sqlIdentifier;
                } else {
                    return null;
                }
            case OR:
            case AND:
                SqlBasicCall call = (SqlBasicCall)cloneNode;
                SqlNode[] nodes = call.getOperands();
                List<SqlNode> sqlNodeList = new ArrayList<>(nodes.length);
                for (int i = 0; i < nodes.length; i++) {
                    SqlNode node = reduce(nodes[i], alias);
                    if (node != null) {
                        sqlNodeList.add(node);
                    }
                }
                if (sqlNodeList.size() == 1) {
                    SqlBinaryOperator equal
                        = new SqlBinaryOperator("=", SqlKind.EQUALS, 30, true, ReturnTypes.BOOLEAN_NULLABLE,
                            InferTypes.FIRST_KNOWN, OperandTypes.COMPARABLE_UNORDERED_COMPARABLE_UNORDERED);
                    SqlBasicCall andEqual = new SqlBasicCall(equal, createEqualNodes(sqlKind), new SqlParserPos(0, 0));
                    sqlNodeList.add(andEqual);
                    return call.getOperator().createCall(call.getFunctionQuantifier(), call.getParserPosition(),
                        sqlNodeList.toArray(new SqlNode[sqlNodeList.size()]));
                } else if (sqlNodeList.size() > 1) {
                    return call.getOperator().createCall(call.getFunctionQuantifier(), call.getParserPosition(),
                        sqlNodeList.toArray(new SqlNode[sqlNodeList.size()]));
                } else {
                    return null;
                }

            default:
                if (sqlNode instanceof SqlBasicCall) {
                    SqlBasicCall sqlBasicCall = (SqlBasicCall)cloneNode;
                    SqlNode node = reduce(sqlBasicCall.getOperands()[0], alias);
                    if (node == null) {
                        return null;
                    } else {
                        SqlBasicCall basicCall
                            = (SqlBasicCall)sqlBasicCall.getOperator().createCall(sqlBasicCall.getFunctionQuantifier(),
                                sqlBasicCall.getParserPosition(),
                                Arrays.copyOf(sqlBasicCall.getOperands(), sqlBasicCall.getOperands().length));
                        basicCall.setOperand(0, node);
                        return basicCall;
                    }
                } else {
                    throw new UnsupportedOperationException("can't find tableName");
                }
        }
    }

    private static SqlNodeList reduce(SqlNodeList sqlNodes, String alias) {
        if (sqlNodes == null) {
            return sqlNodes;
        }
        SqlNodeList nodes = sqlNodes.clone(new SqlParserPos(0, 0));
        List<SqlNode> newNodes = new ArrayList<>(nodes.size());
        Iterator<SqlNode> sqlNodeIterable = nodes.iterator();
        while (sqlNodeIterable.hasNext()) {
            SqlNode sqlNode = sqlNodeIterable.next();
            sqlNode = reduce(sqlNode, alias);
            if (sqlNode != null) {
                newNodes.add(sqlNode);
            }
        }
        if (newNodes.size() > 0) {
            return new SqlNodeList(newNodes, nodes.getParserPosition());
        } else {
            return null;
        }
    }

    private static SqlNodeList changeTableName(SqlNodeList sqlNodes, String alias) {
        if (sqlNodes == null) {
            return sqlNodes;
        }
        SqlNodeList nodes = sqlNodes.clone(new SqlParserPos(0, 0));
        List<SqlNode> newNodes = new ArrayList<>(nodes.size());
        Iterator<SqlNode> sqlNodeIterable = nodes.iterator();
        while (sqlNodeIterable.hasNext()) {
            SqlNode sqlNode = sqlNodeIterable.next();
            sqlNode = changeTableName(sqlNode, alias);
            newNodes.add(sqlNode);
        }
        return new SqlNodeList(newNodes, nodes.getParserPosition());
    }

    public static SqlNode changeTableName(SqlNode sqlNode, String alias) {
        if (sqlNode == null){
            return null;
        }
        SqlKind sqlKind = sqlNode.getKind();
        switch (sqlKind) {
            case IDENTIFIER:
                SqlIdentifier sqlIdentifier = new SqlIdentifier(
                    new ArrayList<>(((SqlIdentifier)sqlNode).names.asList()), sqlNode.getParserPosition());
                return sqlIdentifier.setName(0, alias);
            case OR:
            case AND:
                SqlBasicCall call = (SqlBasicCall)sqlNode;
                SqlNode[] nodes = call.getOperands();
                List<SqlNode> sqlNodeList = new ArrayList<>(nodes.length);
                for (int i = 0; i < nodes.length; i++) {
                    SqlNode node = changeTableName(nodes[i], alias);
                    sqlNodeList.add(node);
                }
                return call.getOperator().createCall(call.getFunctionQuantifier(), call.getParserPosition(),
                    sqlNodeList.toArray(new SqlNode[sqlNodeList.size()]));

            default:
                if (sqlNode instanceof SqlBasicCall) {
                    SqlBasicCall sqlBasicCall = (SqlBasicCall)sqlNode;
                    SqlNode node = changeTableName(sqlBasicCall.getOperands()[0], alias);
                    SqlBasicCall basicCall
                        = (SqlBasicCall)sqlBasicCall.getOperator().createCall(sqlBasicCall.getFunctionQuantifier(),
                            sqlBasicCall.getParserPosition(),
                            Arrays.copyOf(sqlBasicCall.getOperands(), sqlBasicCall.getOperands().length));
                    basicCall.setOperand(0, node);
                    return basicCall;
                } else {
                    throw new UnsupportedOperationException("don't support " +sqlNode);
                }
        }
    }

    public static SqlNode[] createEqualNodes(SqlKind sqlKind) {
        SqlNode[] nodes = new SqlNode[2];
        if (SqlKind.AND == sqlKind) {
            nodes[0] = SqlLiteral.createExactNumeric("1", new SqlParserPos(0, 0));
            nodes[1] = SqlLiteral.createExactNumeric("1", new SqlParserPos(0, 0));
        } else {
            nodes[0] = SqlLiteral.createExactNumeric("0", new SqlParserPos(0, 0));
            nodes[1] = SqlLiteral.createExactNumeric("1", new SqlParserPos(0, 0));
        }
        return nodes;
    }

}
