package com.kang.order.po.order;

import lombok.Data;

import java.util.Date;

@Data
public class OrderDO {
    private Long id;

    private String orderName;

    private Boolean deleted;

    private String creator;

    private String modifier;

    private Date gmtCreated;

    private Date gmtModified;

}
