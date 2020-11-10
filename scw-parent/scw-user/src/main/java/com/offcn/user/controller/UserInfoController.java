package com.offcn.user.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.user.po.TMember;
import com.offcn.user.service.UserService;

import com.offcn.user.vo.req.UserRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@Component
public class UserInfoController {
    @Autowired
    private UserService userService;
    @GetMapping("/findUser/{id}")
    public AppResponse<UserRespVo> findUser(@PathVariable("id") Integer id){
        TMember member = userService.findTmemberById(id);
        UserRespVo userRespVo = new UserRespVo();
        BeanUtils.copyProperties(member,userRespVo);
        return AppResponse.ok(userRespVo);
    }
}
