package com.bruce.dusharding.strategy;

import java.util.List;
import java.util.Map;

/**
 * strategy for sharding.
 * @date 2024/7/27
 */
public interface ShardingStrategy {

    List<String> getShardingColumns();

    String doSharding(List<String> availableTargetNames, String localTableName, Map<String, Object> shardingParams);

}
