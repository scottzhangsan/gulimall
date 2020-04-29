package com.atguigu.gulimall.search.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {


    //到达首页
    @GetMapping(value = {"/","/index.html"})
    public String index(Model model){

        return "index" ;
    }

}
