package com.offcn.user.controller;

import com.netflix.discovery.converters.Auto;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.user.component.SmsTemplate;
import com.offcn.user.po.TMember;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.req.UserRegistVo;
import com.offcn.user.vo.req.UserRespVo;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Api(tags = "用户登录/注册模块(包括忘记密码等)")
@Slf4j
public class UserLoginController {
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @ApiOperation("获取注册验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone",value = "手机号",required = true)
    })
    @PostMapping("/sendCode")
    public AppResponse<Object> sendCode(String phone){
        String code = UUID.randomUUID().toString().substring(0, 4);
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
        HashMap<String, String> querys = new HashMap<String, String>();
        querys.put("phone", phone);
        querys.put("templateId", "TP18040316");
        querys.put("variable", "code:"+code);
      /*  querys.put("mobile",phone);
        querys.put("param","code:"+code);
        querys.put("tpl_id", "TP1711063");//短信模板*/
        String sendcode = smsTemplate.send(querys);
      if(sendcode.equals("") || sendcode.equals("fail")){
            return AppResponse.fail("短信发送失败");
        }
    return AppResponse.ok(sendcode);
    }




    @ApiOperation("用户注册")
    @PostMapping("/regist")
    public AppResponse<Object> regist(UserRegistVo registVo){
        String code = redisTemplate.opsForValue().get(registVo.getLoginacct());
        if (!StringUtils.isEmpty(code)){
            boolean b = code.equalsIgnoreCase(registVo.getCode());
            if (b){
                TMember member = new TMember();
                BeanUtils.copyProperties(registVo,member);
             try {
                    userService.registerUser(member);
                    log.debug("用户信息注册成功:[]",member.getLoginacct());
                    redisTemplate.delete(registVo.getLoginacct());
                    return AppResponse.ok("注册成功....");
                } catch (Exception e) {
                    log.error("用户信息注册失败:{}",member.getLoginacct());
                    return AppResponse.fail(e.getMessage());
                }

            }else {
                return AppResponse.fail("验证码错误");
            }

        }else {
            return AppResponse.fail("验证码过期,请重新获取");
        }

    }



    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户名",required = true),
            @ApiImplicitParam(name = "password",value = "密码",required = true)
    })
    @GetMapping("/login")
    public AppResponse<UserRespVo> login(String username,String password){
        TMember member = userService.login(username,password);
        if (member==null){
            AppResponse<UserRespVo> fail = AppResponse.fail(null);
            fail.setMsg("用户名密码错误");
            return fail;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        System.out.println(token);
        UserRespVo vo = new UserRespVo();
        BeanUtils.copyProperties(member,vo);
        vo.setAccessToken(token);
        redisTemplate.opsForValue().set(token,member.getId()+"",2,TimeUnit.HOURS);
        return AppResponse.ok(vo);

    }


}
