package com.dfire.platform.alchemy.connector.redis;

import com.dfire.platform.alchemy.client.request.SqlSubmitFlinkRequest;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.connector.BaseConnectorTest;
import com.dfire.platform.alchemy.descriptor.RedisSinkDescriptor;
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
 * @date 2019/5/28
 */
public class RedisSinkTest extends BaseConnectorTest {

    @Test
    public void simpleWrite() throws Exception {
        Response response = execute("insert into redis_sink select * from csv_table_test", "set");
        assert response.isSuccess();
    }

    @Test
    public void groupByWrite() throws Exception {
        Response response = execute("insert into redis_sink  select c.first,sum(c.score) from csv_table_test  c  group by c.first ", "set");
        assert response.isSuccess();
    }

    @Test
    public void hset() throws Exception {
        Response response = execute("insert into redis_sink  select * from csv_table_test", "hset");
        assert response.isSuccess();
    }

    @Test
    public void rpush() throws Exception {
        Response response = execute("insert into redis_sink  select * from csv_table_test", "rpush");
        assert response.isSuccess();
    }

    @Test
    public void sadd() throws Exception {
        Response response = execute("insert into redis_sink  select * from csv_table_test", "sadd");
        assert response.isSuccess();
    }

    @Test
    public void zadd() throws Exception {
        Response response = execute("insert into redis_sink  select * from csv_table_test", "zadd");
        assert response.isSuccess();
    }

    Response execute(String sql, String command) throws Exception {
        File sqlJobFile = ResourceUtils.getFile("classpath:yaml/sql.yaml");
        SqlSubmitFlinkRequest sqlSubmitFlinkRequest
            = BindPropertiesUtil.bindProperties(sqlJobFile, SqlSubmitFlinkRequest.class);
        SourceDescriptor sourceDescriptor = createSource("csv_table_test", "classpath:yaml/csv-source.yaml", SourceType.CSV, TableType.TABLE);
        SinkDescriptor sinkDescriptor = createSink("redis_sink", "classpath:yaml/redis-sink.yaml", SinkType.REDIS);
        RedisSinkDescriptor redisSinkDescriptor = (RedisSinkDescriptor) sinkDescriptor;
        redisSinkDescriptor.setCommand(command);
        sqlSubmitFlinkRequest.setSources(Lists.newArrayList(sourceDescriptor));
        sqlSubmitFlinkRequest.setSinks(Lists.newArrayList(redisSinkDescriptor));
        sqlSubmitFlinkRequest.setSqls(SqlParseUtil.findQuerySql(Lists.newArrayList(sql)));
        return client.submit(sqlSubmitFlinkRequest);
    }
}
