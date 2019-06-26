package com.dfire.platform.alchemy.connectors.tsdb.handler;

import com.dfire.platform.alchemy.connectors.tsdb.TsdbData;

import java.io.IOException;

/**
 * @author congbai
 * @date 2018/8/8
 */
public interface TsdbHandler{

    void execute(TsdbData tsdbData);

    void close() throws IOException;
}
