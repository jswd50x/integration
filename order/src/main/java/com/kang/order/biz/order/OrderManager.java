package com.kang.order.biz.order;

import com.kang.order.mapper.order.OrderMapper;
import com.kang.order.po.order.OrderDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderManager {
    @Autowired private OrderMapper orderMapper;
    public void save(){
        OrderDO orderDO=new OrderDO();
        orderDO.setOrderName("title");
        orderMapper.save(orderDO);
    }
}
