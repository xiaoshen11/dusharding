package com.bruce.dusharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.bruce.dusharding.config.ShardingProperties;
import com.bruce.dusharding.demo.model.User;
import com.bruce.dusharding.strategy.HashShardingStrategy;
import com.bruce.dusharding.strategy.ShardingStrategy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2024/7/27
 */
public class StandardShardingEngine implements ShardingEngine {

    // 数据库的和表映射
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    // 表和数据库映射
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    // 分库规则
    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
    // 分表规则
    private final Map<String, ShardingStrategy> tableStrategies = new HashMap<>();

    public StandardShardingEngine(ShardingProperties properties){
        properties.getTables().forEach((table, tableProperties) ->{
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0], tableName = split[1];
                actualDatabaseNames.add(databaseName, tableName);
                actualTableNames.add(tableName, databaseName);
            });
            databaseStrategies.put(table,new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategies.put(table,new HashShardingStrategy(tableProperties.getTableStrategy()));
        });
    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        if(sqlStatement instanceof SQLInsertStatement sqlInsertStatement){
            String table = sqlInsertStatement.getTableName().getSimpleName();
            Map<String,Object> shardingColumnsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr)column;
                String simpleName = columnExpr.getSimpleName();
                shardingColumnsMap.put(simpleName, args[i]);
            }

            ShardingStrategy databaseStrategy = databaseStrategies.get(table);
            String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);
            ShardingStrategy tableStrategy = tableStrategies.get(table);
            String targetTable = tableStrategy.doSharding(actualTableNames.get(table), table, shardingColumnsMap);

            System.out.println(" ====>> target db.table = " + targetDatabase + "." + targetTable);

        }else{


        }

        Object parameterObject = args[0];
        System.out.println(" ====>>> getObject sql statement: " + sql);
        int id = 0;
        if (parameterObject instanceof User user) {
            id = user.getId();
        } else if (parameterObject instanceof Integer uid) {
            id = uid;
        }

        return new ShardingResult(id % 2 == 0 ? "ds0" : "ds1", sql);
    }
}
