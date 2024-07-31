package com.bruce.dusharding.mybatis;

import com.bruce.dusharding.engine.ShardingContext;
import com.bruce.dusharding.engine.ShardingResult;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * interceptor sql.
 * @date 2024/7/25
 */
@Component
@Intercepts(@org.apache.ibatis.plugin.Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {java.sql.Connection.class, Integer.class}
        )
)
public class SqlStatementInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ShardingResult result = ShardingContext.get();
        if(result != null){
            StatementHandler handler = (StatementHandler)invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String sql = boundSql.getSql();
            System.out.println(" ====>>> sql statement: " + sql);

            // 修改sql
            String targetSqlStatement = result.getTargetSqlStatement();
            if(!sql.equalsIgnoreCase(targetSqlStatement)){
                replaceSql(targetSqlStatement, boundSql);
            }
        }

        return invocation.proceed();
    }

    private static void replaceSql(String sql, BoundSql boundSql) throws NoSuchFieldException {
        Field field = boundSql.getClass().getDeclaredField("sql");
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        long fieldOffset = unsafe.objectFieldOffset(field);
        unsafe.putObject(boundSql, fieldOffset, sql);
    }
}
