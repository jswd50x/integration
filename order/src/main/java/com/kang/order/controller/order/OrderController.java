package com.kang.order.controller.order;

import com.kang.order.biz.order.OrderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/order")
public class OrderController {
    @Autowired
    private OrderManager orderManager;

    @GetMapping(value = "/save")
    public String save() {
        orderManager.save();
        return "success";
    }
}
