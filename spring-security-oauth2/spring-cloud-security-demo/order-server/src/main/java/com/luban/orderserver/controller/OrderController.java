package com.luban.orderserver.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fox
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @RequestMapping("/selectOrderInfoByIdAndUsername")
    public String selectOrderInfoByIdAndUsername(long id,String username) {

        return "获取订单信息成功";
    }
}
