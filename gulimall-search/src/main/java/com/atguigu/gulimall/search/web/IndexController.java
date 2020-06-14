package com.atguigu.gulimall.search.web;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.PageVo;
import com.atguigu.gulimall.search.vo.QueryVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("result",mallSearchService.search(param) ) ;
        return "list" ;
    }
    @PostMapping("/test")
    @ResponseBody
    public Map<String,String> test( @RequestBody @Valid PageVo<QueryVo> vo , BindingResult result){
        Map<String,String> map = new HashMap<>() ;
        if (result.hasErrors()){
            List<ObjectError> allErrors = result.getAllErrors();

            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError item : fieldErrors) {
                map.put(item.getField(),item.getDefaultMessage());
            }

        }
        return  map ;
    }

}
