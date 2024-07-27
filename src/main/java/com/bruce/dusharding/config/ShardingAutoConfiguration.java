package com.bruce.dusharding.config;

import com.bruce.dusharding.datasource.ShardingDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * auto configration
 * @date 2024/7/25
 */
@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties properties){
        return new ShardingDataSource(properties);
    }

}
