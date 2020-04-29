package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {


    @Autowired
    private CategoryService categoryService ;



    //到达首页
    @GetMapping(value = {"/","/index.html"})
    public String index(Model model){
        model.addAttribute("categorys",categoryService.getLevel1CategoryCategory()) ;
        return "index" ;
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<Long, List<Catalog2Vo>> getCatalogJson(){
        return  categoryService.getCatalogJson() ;
    }
}
