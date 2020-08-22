package com.lvym.esjd.controller;

import com.lvym.esjd.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }

    @Autowired
    private IndexService indexService;

    @GetMapping("/parsejd/{keyword}")
    @ResponseBody
    public Boolean parsejd(@PathVariable String keyword) throws IOException {
        return indexService.parsejd(keyword);
    }

    @GetMapping("/searchindex/{keyword}/{pageNo}/{pageSize}")
    @ResponseBody
    public List<Map<String,Object>> searchindex(@PathVariable String keyword,@PathVariable int pageNo,@PathVariable int pageSize) throws IOException {
        return indexService.searchindexhighlightBuilder(keyword,pageNo,pageSize);
    }
}
