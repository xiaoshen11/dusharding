package com.bruce.dusharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.bruce.dusharding.config.ShardingProperties;
import com.bruce.dusharding.demo.model.User;
import com.bruce.dusharding.strategy.HashShardingStrategy;
import com.bruce.dusharding.strategy.ShardingStrategy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        String table;
        Map<String,Object> shardingColumnsMap;

        if(sqlStatement instanceof SQLInsertStatement sqlInsertStatement){
            table = sqlInsertStatement.getTableName().getSimpleName();
            shardingColumnsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr)column;
                String simpleName = columnExpr.getSimpleName();
                shardingColumnsMap.put(simpleName, args[i]);
            }
        }else{
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);

            LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if(sqlNames.size() > 1){
                throw new RuntimeException("not support multi tables shading:" + sqlNames);
            }
            table = sqlNames.iterator().next().getSimpleName();
            System.out.println(" ====>> visitor.getOriginalTables = " + table);

            shardingColumnsMap = visitor.getConditions().stream().collect(Collectors.toMap(
                    c -> c.getColumn().getName(), c -> c.getValues().get(0)));

            System.out.println(" ====>> visitor.getConditions = " + shardingColumnsMap);

        }

        ShardingStrategy databaseStrategy = databaseStrategies.get(table);
        String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);
        ShardingStrategy tableStrategy = tableStrategies.get(table);
        String targetTable = tableStrategy.doSharding(actualTableNames.get(table), table, shardingColumnsMap);

        System.out.println(" ====>> target db.table = " + targetDatabase + "." + targetTable);

        return new ShardingResult(targetDatabase, sql.replace(table, targetTable));
    }
}
