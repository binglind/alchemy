package com.dfire.platform.alchemy.api.common;

import java.io.Serializable;

/**
 * @author congbai
 * @date 2019/5/20
 */
public class Alias implements Serializable {

    private static final long serialVersionUID = 1L;
    private String table;

    private String alias;

    public Alias(String table, String alias) {
        this.table = table;
        this.alias = alias;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
