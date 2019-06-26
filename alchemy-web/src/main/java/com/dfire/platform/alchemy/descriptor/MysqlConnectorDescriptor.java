package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.api.common.Side;
import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.common.Field;
import com.dfire.platform.alchemy.connectors.common.side.SideTable;
import com.dfire.platform.alchemy.connectors.mysql.side.MysqlAsyncSideFunction;
import com.dfire.platform.alchemy.connectors.mysql.side.MysqlSideProperties;
import com.dfire.platform.alchemy.connectors.mysql.side.MysqlSyncSideFunction;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author congbai
 * @date 2019/5/24
 */
public class MysqlConnectorDescriptor implements ConnectorDescriptor {

    private String url;

    private String username;

    private String password;

    private Integer maxPoolSize;

    @Override
    public <T> T buildSource(List<Field> schema, FormatDescriptor format) throws Exception {
        return buildSource(schema, format, null);
    }

    @Override
    public <T, R> T buildSource(List<Field> schema, FormatDescriptor format, R param) throws Exception {
        if (!(param instanceof SideTable)) {
            throw new IllegalArgumentException("MysqlConnectorDescriptor's param must be SideTable");
        }
        SideTable sideTableInfo = (SideTable)param;
        if (sideTableInfo == null) {
            throw new UnsupportedOperationException("don't support read data from mysql");
        }
        Side side = sideTableInfo.getSide();
        if (side == null) {
            throw new IllegalArgumentException("MysqlConnectorDescriptor's side info is null");
        }
        MysqlSideProperties properties = createProperties();
        if (side.isAsync()) {
            return (T)new MysqlAsyncSideFunction(sideTableInfo, properties);
        } else {
            return (T)new MysqlSyncSideFunction(sideTableInfo, properties);
        }
    }

    private MysqlSideProperties createProperties() {
        MysqlSideProperties properties = new MysqlSideProperties();
        properties.setUrl(this.url);
        properties.setUsername(this.username);
        properties.setPassword(this.password);
        properties.setMaxPoolSize(this.maxPoolSize);
        return properties;
    }

    @Override
    public String type() {
        return Constants.CONNECTOR_TYPE_VALUE_MYSQL;
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(url, "mysql的url不能为空");
        Assert.notNull(username, "mysql的userName不能为空");
        Assert.notNull(password, "mysql的password不能为空");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
}
