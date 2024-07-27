package com.bruce.dusharding.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bruce.dusharding.engine.ShardingContext;
import com.bruce.dusharding.engine.ShardingResult;
import com.bruce.dusharding.config.ShardingProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * sharding datasource
 * @date 2024/7/25
 */
public class ShardingDataSource extends AbstractRoutingDataSource {

    public ShardingDataSource(ShardingProperties properties){
        Map<Object, Object> datasourceMap = new LinkedHashMap<>();
        properties.getDatasources().forEach(
                (k, v) -> {
                    try {
                        datasourceMap.put(k, DruidDataSourceFactory.createDataSource(v));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        setTargetDataSources(datasourceMap);
        setDefaultTargetDataSource(datasourceMap.values().iterator().next());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult shardingResult = ShardingContext.get();
        String key = shardingResult == null ? null : shardingResult.getTargetDataSourceName();
        System.out.println(" determineCurrentLookupKey = " + key);
        return key;
    }
}
