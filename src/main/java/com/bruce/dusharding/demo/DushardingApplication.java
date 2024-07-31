package com.bruce.dusharding.demo;

import com.bruce.dusharding.config.ShardingAutoConfiguration;
import com.bruce.dusharding.demo.mapper.OrderMapper;
import com.bruce.dusharding.demo.model.Order;
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

    @Autowired
    OrderMapper orderMapper;

    @Bean
    ApplicationRunner applicationRunner(){
        return x -> {

            System.out.println(" =================>  =================>  =================>");
            System.out.println(" =================>  test user sharding  =================>");
            System.out.println(" =================>  =================>  =================>");
            for (int id = 1; id <= 60; id++) {
                testUser(id);

            }

            System.out.println("\n\n\n\n");

            System.out.println(" =================>  =================>  =================>");
            System.out.println(" =================>  test order sharding  =================>");
            System.out.println(" =================>  =================>  =================>");

            for (int id = 1; id <= 40; id++) {
                testOrder(id);
            }
        };
    }

    private void testUser(int id) {
        System.out.println(" \n\n =================> id = " + id);
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

        System.out.println(" ===> 4. test delete ...");
        int delete = userMapper.delete(id);
        System.out.println(" ===> delete = " + delete);
    }

    private void testOrder(int id) {
        int id2 = id + 100;
        System.out.println(" \n\n =================> id = " + id);
        System.out.println(" ===> 1. test insert ...");
        int insert = orderMapper.insert(new Order(id, 1, 10d));
        System.out.println(" ===> insert = " + insert);
        insert = orderMapper.insert(new Order(id2, 2, 10d));
        System.out.println(" ===> insert = " + insert);

        System.out.println(" ===> 2. test find ...");
        Order order1 = orderMapper.findById(id,1);
        System.out.println(" ===> find = " + order1);
        Order order2 = orderMapper.findById(id2,2);
        System.out.println(" ===> find = " + order2);

        System.out.println(" ===> 3. test update ...");
        order1.setPrice(11d);
        int update = orderMapper.update(order1);
        System.out.println(" ===> update = " + update);
        order2.setPrice(22d);
        update = orderMapper.update(order2);
        System.out.println(" ===> update = " + update);


        Order order11 = orderMapper.findById(id,1);
        System.out.println(" ===> find = " + order11);
        Order order22 = orderMapper.findById(id2,2);
        System.out.println(" ===> find = " + order22);

        System.out.println(" ===> 4. test delete ...");
        int delete = orderMapper.delete(id,1);
        System.out.println(" ===> delete = " + delete);
        delete = orderMapper.delete(id2,2);
        System.out.println(" ===> delete = " + delete);
    }

}
