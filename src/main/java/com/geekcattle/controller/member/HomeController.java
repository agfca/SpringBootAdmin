/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.geekcattle.controller.member;

import com.geekcattle.model.member.Member;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * author geekcattle
 * date 2017/3/14 0014 上午 9:54
 */
@Controller
@RequestMapping("/member")
public class HomeController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/index")
    public String index(Model model){
        Member member = (Member) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        String account = member.getAccount();
        model.addAttribute("account", account);
        return "member/home";
    }

    /**
     * 登录页面
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(){
        try {
            Boolean isAuth = SecurityUtils.getSubject().isAuthenticated();
            if(isAuth){
                return "redirect:/member/index";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "member/login";
    }

    /**
     * 注册页面
     * @return
     */
    @RequestMapping(value = "/reg", method = RequestMethod.GET)
    public String reg(){
        try {
            Boolean isAuth = SecurityUtils.getSubject().isAuthenticated();
            if(isAuth){
                return "redirect:/member/index";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "member/reg";
    }

}
