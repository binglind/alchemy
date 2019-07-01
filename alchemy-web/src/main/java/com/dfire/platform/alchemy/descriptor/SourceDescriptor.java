package com.dfire.platform.alchemy.descriptor;

import com.dfire.platform.alchemy.api.common.Side;
import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.common.Field;
import com.dfire.platform.alchemy.connectors.common.side.SideTable;
import com.dfire.platform.alchemy.domain.Source;
import com.dfire.platform.alchemy.domain.enumeration.SourceType;
import com.dfire.platform.alchemy.domain.enumeration.TableType;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author congbai
 * @date 02/06/2018
 */
public class SourceDescriptor implements CoreDescriptor<SideTable> {

    private String name;

    private TableType tableType;

    private List<Field> schema;

    private String sql;

    private Map<String, Object> connector;

    private volatile ConnectorDescriptor connectorDescriptor;

    private Side side;

    private FormatDescriptor format;

    public static void validate(Source source) throws Exception {
        SourceDescriptor sourceDescriptor = from(source);
        sourceDescriptor.validate();
    }

    public static SourceDescriptor from(Source source) throws Exception {
        TableType tableType = source.getTableType();
        SourceType sourceType = source.getSourceType();
        SourceDescriptor sourceDescriptor;
        if (tableType == TableType.VIEW) {
            sourceDescriptor = new SourceDescriptor();
            sourceDescriptor.setSql(source.getConfig());
        } else {
            sourceDescriptor = BindPropertiesUtil.bindProperties(source.getConfig(), SourceDescriptor.class);
            ConnectorDescriptor descriptor
                = DescriptorFactory.me.find(sourceType.toString().toLowerCase(), ConnectorDescriptor.class);
            if (descriptor == null) {
                throw new UnsupportedOperationException("Unknow source type:" + sourceType);
            }
            ConnectorDescriptor connectorDescriptor = BindPropertiesUtil.bindProperties(sourceDescriptor.getConnector(), descriptor.getClass());
            sourceDescriptor.setConnectorDescriptor(connectorDescriptor);

        }
        sourceDescriptor.setName(source.getName());
        sourceDescriptor.setTableType(tableType);
        return sourceDescriptor;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public <T> T transform() throws Exception {
        if (this.schema == null) {
            throw new IllegalArgumentException("table schema is null");
        }
        if (this.getConnectorDescriptor() != null) {
            return this.getConnectorDescriptor().buildSource(this.schema, this.format);
        }
        return null;
    }

    @Override
    public <T> T transform(SideTable param) throws Exception {
        if (this.schema == null) {
            throw new IllegalArgumentException("table schema is null");
        }
        if (this.getConnectorDescriptor() != null) {
            return this.getConnectorDescriptor().buildSource(this.schema, this.format, param);
        }
        return null;
    }

    public Map<String, Object> getConnector() {
        return connector;
    }

    public void setConnector(Map<String, Object> connector) {
        this.connector = connector;
    }

    public ConnectorDescriptor getConnectorDescriptor() {
        if (this.connectorDescriptor == null) {
            synchronized (this) {
                if (this.connector == null) {
                    return this.connectorDescriptor;
                }
                Object type = this.connector.get(Constants.DESCRIPTOR_TYPE_KEY);
                if (type == null) {
                    return this.connectorDescriptor;
                }

                if (connectorDescriptor == null) {
                    return this.connectorDescriptor;
                }
                try {
                    this.connectorDescriptor
                        = BindPropertiesUtil.bindProperties(this.connector, connectorDescriptor.getClass());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return connectorDescriptor;
    }

    public void setConnectorDescriptor(ConnectorDescriptor connectorDescriptor) {
        this.connectorDescriptor = connectorDescriptor;
    }

    public FormatDescriptor getFormat() {
        return format;
    }

    public void setFormat(FormatDescriptor format) {
        this.format = format;
    }

    public List<Field> getSchema() {
        return schema;
    }

    public void setSchema(List<Field> schema) {
        this.schema = schema;
    }

    public TableType getTableType() {
        return tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String type() {
        return Constants.TYPE_VALUE_SOURCE;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(name, "the table name is null");
        if (TableType.VIEW == tableType) {
            Assert.notNull(sql, "view sql is null");
        } else {
            Assert.notNull(schema, "the table schema is null");
            Assert.notNull(format, "the table format is null");
            Assert.notNull(connectorDescriptor, "the table connector is null");
            connectorDescriptor.validate();
        }

    }

}
