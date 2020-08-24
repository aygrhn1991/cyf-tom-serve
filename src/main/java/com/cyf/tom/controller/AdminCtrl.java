package com.cyf.tom.controller;

import com.cyf.tom.model.Admin;
import com.cyf.tom.suit.request.Search;
import com.cyf.tom.suit.response.R;
import com.cyf.tom.suit.response.Result;
import com.cyf.tom.suit.util.UtilPage;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminCtrl {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbc;

    @RequestMapping("/test/{message}")
    public Result test(@PathVariable String message) {
        return R.success("ok", message);
    }

    private Admin getAdminFromCookie() throws UnsupportedEncodingException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String json = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("admin")) {
                json = URLDecoder.decode(cookie.getValue(), "UTF-8");
            }
        }
        return new Gson().fromJson(json, Admin.class);
    }

    //region 登录
    @RequestMapping("/doLogin")
    @ResponseBody
    public Result doLogin(@RequestBody Admin model) {
        String sql = "select t.* from tom_admin t where t.account=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, model.account);
        if (list.size() == 0) {
            return R.error("账号不存在");
        }
        if (!list.get(0).get("account").toString().equals(model.password)) {
            return R.error("密码错误");
        }
        return R.success("登录成功", list.get(0));
    }

    @RequestMapping("/getAdmin")
    @ResponseBody
    public Result getAdmin() throws UnsupportedEncodingException {
        String account = this.getAdminFromCookie().account;
        String sql = "select t.* from tom_admin t where t.account=?";
        List<Map<String, Object>> list = this.jdbc.queryForList(sql, account);
        return R.success("管理员信息", list.get(0));
    }
    //endregion

    //region 页面
    @RequestMapping("/login")
    public String login() {
        return "admin/login";
    }

    @RequestMapping("/index")
    public String index() {
        return "admin/index";
    }

    @RequestMapping("/consult")
    public String consult() {
        return "admin/consult";
    }
    //endregion

    //region 接口
    @RequestMapping("/getConsult")
    @ResponseBody
    public Result getConsult(@RequestBody Search model) {
        String sql1 = "select * from tom_consult t where t.del=0 ";
        String sql2 = "select count(*) from tom_consult t where t.del=0 ";
        if (!(model.string1 == null || model.string1.isEmpty())) {
            sql1 += " and t.name like '%" + model.string1 + "%' ";
            sql2 += " and t.name like '%" + model.string1 + "%' ";
        }
        if (!(model.string2 == null || model.string2.isEmpty())) {
            sql1 += " and t.phone like '%" + model.string2 + "%' ";
            sql2 += " and t.phone like '%" + model.string2 + "%' ";
        }
        int count = this.jdbc.queryForObject(sql2, Integer.class);
        sql1 += " order by t.systime desc limit " + UtilPage.getPage(model);
        List<Map<String, Object>> data = this.jdbc.queryForList(sql1);
        return R.success("咨询信息", count, data);
    }

    @RequestMapping("/deleteConsult/{id}")
    @ResponseBody
    public Result deleteConsult(@PathVariable int id) {
        String sql = "update tom_consult t set t.del=1 where t.id=?";
        int count = this.jdbc.update(sql, id);
        return R.success("操作成功");
    }

    @RequestMapping("/setConsultState/{id}")
    @ResponseBody
    public Result setConsultState(@PathVariable int id) {
        String sql = "update tom_consult t set t.state=1 where t.id=?";
        int count = this.jdbc.update(sql, id);
        return R.success("操作成功");
    }
    //endregion
}
