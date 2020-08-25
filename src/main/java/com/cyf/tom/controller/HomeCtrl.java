package com.cyf.tom.controller;

import com.cyf.tom.model.Admin;
import com.cyf.tom.model.Consult;
import com.cyf.tom.suit.response.R;
import com.cyf.tom.suit.response.Result;
import com.cyf.tom.suit.util.UtilPage;
import com.cyf.tom.suit.util.UtilPageOfJava;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.*;

@Controller
@RequestMapping(value = {"/home"})
public class HomeCtrl {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbc;

    @RequestMapping("/test/{message}")
    @ResponseBody
    public Result test(@PathVariable String message) {
        return R.success("ok", message);
    }

    //region 页面
    @RequestMapping("/consult1")
    public String consult() {
        return "home/consult1";
    }
    @RequestMapping("/consult2")
    public String consult2() {
        return "home/consult2";
    }

    @RequestMapping("/addConsult")
    @ResponseBody
    public Result addConsult(@RequestBody Consult model) {
        String sql = "insert into tom_consult(name,phone,email,city,message,state,del,systime) values(?,?,?,?,?,0,0,now())";
        int count = this.jdbc.update(sql, model.name, model.phone, model.email, model.city, model.message);
        return R.success("您的信息已提交");
    }

}
