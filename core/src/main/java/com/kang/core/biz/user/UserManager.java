package com.kang.core.biz.user;

import com.kang.core.mapper.user.UserDOMapper;
import com.kang.core.po.user.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserManager {
    @Autowired
    private UserDOMapper userDOMapper;

    public void addUser() {
        UserDO userDO = new UserDO();
        userDO.setUsername("kangkang");
        userDOMapper.save(userDO);
    }
}
