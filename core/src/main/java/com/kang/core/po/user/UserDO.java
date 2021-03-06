package com.kang.core.po.user;

import lombok.Data;

import java.util.Date;

@Data
public class UserDO {
    private Long id;
    private String username;
    private Boolean deleted;

    private String creator;

    private String modifier;

    private Date gmtCreated;

    private Date gmtModified;
}
