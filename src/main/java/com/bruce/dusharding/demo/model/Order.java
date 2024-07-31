package com.bruce.dusharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order Entity
 * @date 2024/7/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private int id;
    private int uid;
    private double price;


}
