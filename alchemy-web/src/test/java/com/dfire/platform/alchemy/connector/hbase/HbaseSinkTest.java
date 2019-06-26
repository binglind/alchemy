package com.dfire.platform.alchemy.connector.hbase;

import com.dfire.platform.alchemy.client.request.SqlSubmitFlinkRequest;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.connector.BaseConnectorTest;
import com.dfire.platform.alchemy.descriptor.HbaseSinkDescriptor;
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
public class HbaseSinkTest extends BaseConnectorTest {

    @Test
    public void singleFaimly() throws Exception {
        Response response = execute("insert into hbase_sink select * from csv_source", true);
        assert response.isSuccess();
    }


    @Test
    public void multiFaimly() throws Exception {
        Response response = execute("insert into hbase_sink select * from csv_source", false);
        assert response.isSuccess();
    }

    Response execute(String sql, boolean single) throws Exception {
        File sqlJobFile = ResourceUtils.getFile("classpath:yaml/sql.yaml");
        SqlSubmitFlinkRequest sqlSubmitFlinkRequest
            = BindPropertiesUtil.bindProperties(sqlJobFile, SqlSubmitFlinkRequest.class);
        SourceDescriptor sourceDescriptor = createSource("csv_source", "classpath:yaml/csv-source.yaml", SourceType.CSV, TableType.TABLE);
        SinkDescriptor sinkDescriptor = createSink("hbase_sink", "classpath:yaml/hbase-sink.yaml", SinkType.HBASE);
        HbaseSinkDescriptor hbaseSinkDescriptor = (HbaseSinkDescriptor) sinkDescriptor;
        if (single) {
            hbaseSinkDescriptor.setFamilyColumns(null);
        } else {
            hbaseSinkDescriptor.setTableName("hbase-multi");
        }
        sqlSubmitFlinkRequest.setSources(Lists.newArrayList(sourceDescriptor));
        sqlSubmitFlinkRequest.setSinks(Lists.newArrayList(hbaseSinkDescriptor));
        sqlSubmitFlinkRequest.setSqls(SqlParseUtil.findQuerySql(Lists.newArrayList(sql)));
        return client.submit(sqlSubmitFlinkRequest);
    }
}
