package com.zwd.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
@Controller
public class HelloController {

    @RequestMapping(value = "")
    public String index() {
        return "index";
    }
}
