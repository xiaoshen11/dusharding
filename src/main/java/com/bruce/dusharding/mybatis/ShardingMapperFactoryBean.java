package com.bruce.dusharding.mybatis;

import com.bruce.dusharding.engine.ShardingContext;
import com.bruce.dusharding.engine.ShardingResult;
import com.bruce.dusharding.demo.model.User;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Proxy;

/**
 * factory bena for mapper.
 *
 * @date 2024/7/25
 */
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public ShardingMapperFactoryBean() {
    }

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        SqlSession session = getSqlSession();
        Configuration configuration = session.getConfiguration();
        Class<?> clazz = getMapperInterface();

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (p, method, args) -> {
            String mapperId = clazz.getName() + "." + method.getName();
            MappedStatement statement = configuration.getMappedStatement(mapperId);
            BoundSql boundSql = statement.getBoundSql(args);
            System.out.println(" ====>>> sql statement: " + boundSql.getSql());

            Object parameterObject = args[0];
            if (parameterObject instanceof User user) {
                ShardingContext.set(new ShardingResult(user.getId() % 2 == 0 ? "ds0" : "ds1"));
            } else if (parameterObject instanceof Integer id) {
                ShardingContext.set(new ShardingResult(id % 2 == 0 ? "ds0" : "ds1"));
            }

            return method.invoke(proxy, args);
        });
    }

}
