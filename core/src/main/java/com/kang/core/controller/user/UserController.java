package com.kang.core.controller.user;

import com.kang.common.util.DateUtils;
import com.kang.core.biz.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class UserController {
    @Autowired
    private UserManager userManager;

    @GetMapping(value = "/add")
    public String addUser() {
        System.out.printf(DateUtils.format(new Date(), DateUtils.CHN_YMD_HHMM));
        userManager.addUser();
        return "success";
    }
}
