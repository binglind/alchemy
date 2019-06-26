package com.dfire.platform.alchemy.connector.mysql;


import com.dfire.platform.alchemy.client.request.SqlSubmitFlinkRequest;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.connector.BaseConnectorTest;
import com.dfire.platform.alchemy.descriptor.SinkDescriptor;
import com.dfire.platform.alchemy.descriptor.SourceDescriptor;
import com.dfire.platform.alchemy.domain.enumeration.SinkType;
import com.dfire.platform.alchemy.domain.enumeration.SourceType;
import com.dfire.platform.alchemy.domain.enumeration.TableType;
import com.dfire.platform.alchemy.service.util.SqlParseUtil;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;

/**
 * @author congbai
 * @date 2019/5/27
 */
public class MysqlSinkTest extends BaseConnectorTest {

    @Test
    public void insert() throws Exception {
        Response response = execute("insert into mysql_sink select c.id,c.first,c.last,c.score from csv_source as c", "classpath:yaml/mysql-insert-sink.yaml");
        assert response.isSuccess();
    }

    @Test
    public void replace() throws Exception {
        Response response = execute("insert into mysql_sink select c.id,c.first,c.last,c.score from csv_source as c", "classpath:yaml/mysql-replace-sink.yaml");
        assert response.isSuccess();
    }


    Response execute(String sql, String yaml) throws Exception {
        File sqlJobFile = ResourceUtils.getFile("classpath:yaml/sql.yaml");
        SqlSubmitFlinkRequest sqlSubmitFlinkRequest
            = BindPropertiesUtil.bindProperties(sqlJobFile, SqlSubmitFlinkRequest.class);
        SourceDescriptor sourceDescriptor = createSource("csv_source", "classpath:yaml/csv-source.yaml", SourceType.CSV, TableType.TABLE);
        SinkDescriptor sinkDescriptor = createSink("mysql_sink", yaml, SinkType.MYSQL);
        sqlSubmitFlinkRequest.setSources(Lists.newArrayList(sourceDescriptor));
        sqlSubmitFlinkRequest.setSinks(Lists.newArrayList(sinkDescriptor));
        sqlSubmitFlinkRequest.setSqls(SqlParseUtil.findQuerySql(Lists.newArrayList(sql)));
        return client.submit(sqlSubmitFlinkRequest);
    }

}
