package com.goodcol.muses.atest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调试用的Controller
 *
 * @author Mr.kusch
 * @date 2022/12/19 11:08
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 获取code的重定向地址一号
     */
    @RequestMapping("/test1")
    public Object test1(@RequestParam("code") String code) {
        return "test1----code的值是 ---->  " + code;
    }

    /**
     * 获取code的重定向地址二号
     */
    @RequestMapping("/hahahaha")
    public Object hahahaha(@RequestParam("code") String code) {
        return "hahahaha----code的值是 ---->  " + code;
    }
}
