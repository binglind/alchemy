package com.dfire.platform.alchemy.common;

/**
 * @author congbai
 * @date 03/06/2018
 */
public class Field {

    private String name;

    private String type;

    private TimeAttribute rowtime;

    private boolean proctime;

    public Field() {}

    public Field(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public TimeAttribute getRowtime() {
        return rowtime;
    }

    public void setRowtime(TimeAttribute rowtime) {
        this.rowtime = rowtime;
    }

    public boolean isProctime() {
        return proctime;
    }

    public void setProctime(boolean proctime) {
        this.proctime = proctime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
