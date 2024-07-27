package com.bruce.dusharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sharing result.
 * @date 2024/7/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShardingResult {

    private String targetDataSourceName;


}
