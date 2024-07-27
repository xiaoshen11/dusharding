package com.bruce.dusharding.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.stereotype.Component;

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
        StatementHandler handler = (StatementHandler)invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        System.out.println(" ====>>> sql statement: " + boundSql.getSql());

        System.out.println(" ====>>> sql parameters: " + boundSql.getParameterObject());
        // todo 修改sql
        return invocation.proceed();
    }
}
