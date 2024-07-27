package com.bruce.dusharding.engine;

/**
 * core sharding engine.
 * @date 2024/7/27
 */
public interface ShardingEngine {

    ShardingResult sharding(String sql, Object[] args);

}
