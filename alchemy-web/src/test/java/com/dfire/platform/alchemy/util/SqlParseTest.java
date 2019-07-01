package com.dfire.platform.alchemy.util;

import com.dfire.platform.alchemy.service.util.SqlParseUtil;
import com.google.common.collect.Lists;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlParseTest {

    private static final SqlParser.Config CONFIG = SqlParser.configBuilder().setLex(Lex.MYSQL).build();

    @Test
    public void parse() throws SqlParseException {
        String sql1 = "insert into first_sink select * from ngx_log";
        String sql2
            = "insert into test_sink select CASE WHEN 1>0 THEN H.vv ELSE '' END ,h.id, h.age, FLOOR(h.age) ,UDF(h.yyy) as udf, TF(h.xxx) AS tf , t.height as he, (select * from source_nest f where f.id in(1,2,3)) from left_table h join right_table t on t.id = h.id where h.age > 10 ";
        List<String> sqls = Lists.newArrayList(sql1, sql2);
        List<String> sourcs = Lists.newArrayList();
        List<String> udfs = Lists.newArrayList();
        List<String> sinks = Lists.newArrayList();
        SqlParseUtil.parse(sqls, sourcs, udfs, sinks);
        assertThat(sourcs).isEqualTo(Lists.newArrayList("ngx_log","source_nest",  "left_table", "right_table"));
        assertThat(udfs).isEqualTo(Lists.newArrayList("UDF", "TF"));
        assertThat(sinks).isEqualTo(Lists.newArrayList("first_sink", "test_sink"));
        List<String> querySqls = SqlParseUtil.findQuerySql(sqls);
        assertThat(querySqls.get(0)).startsWith("SELECT");
        assertThat(querySqls.get(1)).startsWith("SELECT");
    }
}
