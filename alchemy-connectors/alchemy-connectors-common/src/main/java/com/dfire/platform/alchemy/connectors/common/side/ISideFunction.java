package com.dfire.platform.alchemy.connectors.common.side;

import java.util.List;
import java.util.Optional;

import com.dfire.platform.alchemy.api.common.Side;
import com.dfire.platform.alchemy.connectors.common.Cache;
import com.dfire.platform.alchemy.connectors.common.CacheFactory;
import org.apache.calcite.sql.JoinType;
import org.apache.flink.types.Row;



/**
 * @author congbai
 * @date 2019/5/21
 */
public interface ISideFunction<T> {

    default Cache<T> create(Side side) {
        return CacheFactory.get(side);
    }

    default Optional<T> getOrNull(Cache<T> cache, String key) {
        if (cache == null) {
            return null;
        }
        return cache.get(key);
    }

    default void setOrNull(Cache<T> cache, String key, Optional<T> value) {
        if (cache == null) {
            return;
        }
        cache.put(key, value);
    }

    default List<Row> dealMissKey(Row input, JoinType joinType) {
        if (joinType == JoinType.LEFT) {
            return fillRecord(input, null);
        } else {
            return null;
        }
    }

    List<Row> fillRecord(Row input, T value);


    void close() throws Exception;
}
