package com.bruce.dusharding.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @date 2024/7/25
 */
@Data
@ConfigurationProperties(prefix = "spring.sharding")
public class ShardingProperties {

    private Map<String, Properties> datasources = new LinkedHashMap<>();
    private Map<String, TableProperties> tables = new LinkedHashMap<>();


    @Data
    public static class TableProperties {
        private List<String> actualDataNodes;
        private Properties databaseStrategy;
        private Properties tableStrategy;
    }

}
