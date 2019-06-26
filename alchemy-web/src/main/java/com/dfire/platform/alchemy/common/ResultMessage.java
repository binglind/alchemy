package com.dfire.platform.alchemy.common;

/**
 * @author congbai
 */
public class ResultMessage {

    public static final RespCode OK = new RespCode("200", "成功");

    public static final RespCode SOURCE_EMPTY = new RespCode("10001", "数据源不能为空");

    public static final RespCode SINK_EMPTY = new RespCode("10002", "输出端不能为空");

    public static final RespCode SQL_EMPTY = new RespCode("10003", "sql不能为空");

    public static final RespCode CLUSTER_NOT_EXIST = new RespCode("10004", "指定集群不存在");

    public static final RespCode INVALID_SQL = new RespCode("10005", "sql不合法");


}
