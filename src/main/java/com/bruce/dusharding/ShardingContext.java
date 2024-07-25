package com.bruce.dusharding;

/**
 * sharding
 * @date 2024/7/25
 */
public class ShardingContext {

    private static ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

    public static ShardingResult get(){
        return LOCAL.get();
    }

    public static void set(ShardingResult shardingResult){
        LOCAL.set(shardingResult);
    }

}
