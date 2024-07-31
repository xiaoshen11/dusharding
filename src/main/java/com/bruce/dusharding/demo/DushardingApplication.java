package com.bruce.dusharding.demo;

import com.bruce.dusharding.config.ShardingAutoConfiguration;
import com.bruce.dusharding.mybatis.ShardingMapperFactoryBean;
import com.bruce.dusharding.demo.mapper.UserMapper;
import com.bruce.dusharding.demo.model.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "com.bruce.dusharding.demo.mapper", factoryBean = ShardingMapperFactoryBean.class)
public class DushardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DushardingApplication.class, args);
    }

    @Autowired
    UserMapper userMapper;

    @Bean
    ApplicationRunner applicationRunner(){
        return x -> {
            for (int id = 1; id <= 60; id++) {
                test(id);
            }
        };
    }

    private void test(int id) {
        System.out.println(" ===> 1. test insert ...");
        int insert = userMapper.insert(new User(id, "bruce", 25));
        System.out.println(" ===> insert = " + insert);

        System.out.println(" ===> 2. test find ...");
        User user = userMapper.findById(id);
        System.out.println(" ===> find = " + user);

        System.out.println(" ===> 3. test update ...");
        user.setName("bruce-" + id);
        int update = userMapper.update(user);
        System.out.println(" ===> update = " + update);

        User user2 = userMapper.findById(id);
        System.out.println(" ===> find = " + user2);

//        System.out.println(" ===> 4. test delete ...");
//        int delete = userMapper.delete(id);
//        System.out.println(" ===> delete = " + delete);

    }

}
