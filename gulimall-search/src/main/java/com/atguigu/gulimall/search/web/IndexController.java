package com.atguigu.gulimall.search.web;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired
    private MallSearchService mallSearchService ;

    //到达首页
    @GetMapping(value = {"/","/index.html"})
    public String index(Model model){

        return "list";
    }
    @GetMapping("/list.html")
    public String list(SearchParam param,Model model){
        mallSearchService.search(param) ;
        return "list" ;
    }

}
