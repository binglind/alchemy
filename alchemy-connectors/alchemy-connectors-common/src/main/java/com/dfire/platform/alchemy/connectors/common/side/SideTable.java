package com.dfire.platform.alchemy.connectors.common.side;

import com.dfire.platform.alchemy.api.common.Alias;
import com.dfire.platform.alchemy.api.common.Side;
import org.apache.calcite.sql.JoinType;
import org.apache.flink.api.java.typeutils.RowTypeInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author congbai
 * @date 2019/5/23
 */
public class SideTable implements Serializable {

    private static final long serialVersionUID = 1L;

    private int rowSize;

    private Side side;

    private  String sql;

    private JoinType joinType;

    private  List<String> conditions;

    private  List<Integer> conditionIndexs;

    private Alias sideAlias;

    private  RowTypeInfo sideType;

    public int getRowSize() {
        return rowSize;
    }

    public void setRowSize(int rowSize) {
        this.rowSize = rowSize;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public List<Integer> getConditionIndexs() {
        return conditionIndexs;
    }

    public void setConditionIndexs(List<Integer> conditionIndexs) {
        this.conditionIndexs = conditionIndexs;
    }

    public RowTypeInfo getSideType() {
        return sideType;
    }

    public void setSideType(RowTypeInfo sideType) {
        this.sideType = sideType;
    }

    public Alias getSideAlias() {
        return sideAlias;
    }

    public void setSideAlias(Alias sideAlias) {
        this.sideAlias = sideAlias;
    }
}
