package com.haomostudio.jrs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户账号相关
 * Created by Hermione on 2015/12/26.
 */
@Controller
public class AccountController  {

//    @RequestMapping(value = "/print", method = { RequestMethod.GET })
//    public ModelAndView  printPdf(ModelMap modelMap) {
//        List<User> listUsers=new ArrayList<User>();
//        User user=new User();
//        user.setUsername("张三");
//        user.setAge(15);
//        listUsers.add(user);
//        modelMap.addAttribute("datasource", listUsers);
//        return new ModelAndView("userReport",modelMap);
//    }

    @RequestMapping(value = "/print",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String  printPdf(String test) {
        return "11111";
    }


}


