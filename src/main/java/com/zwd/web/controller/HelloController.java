package com.zwd.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
@Controller
public class HelloController {

    @GetMapping(value = "hello")
    public String index(String name) {
        System.out.println(name);
        return "index";
    }

}
