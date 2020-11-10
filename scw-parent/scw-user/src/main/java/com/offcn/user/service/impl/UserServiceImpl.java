package com.offcn.user.service.impl;

import com.offcn.user.enums.UserExceptionEnum;
import com.offcn.user.exception.UserException;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberExample;
import com.offcn.user.service.UserService;
import org.bouncycastle.crypto.generators.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TMemberMapper memberMapper;

    @Override
    public void registerUser(TMember member) {
        TMemberExample example = new TMemberExample();
        example.createCriteria().andLoginacctEqualTo(member.getLoginacct());
        long l = memberMapper.countByExample(example);
        if (l > 0){
            throw new UserException(UserExceptionEnum.LOGINACCT_EXIST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(member.getUsername());
        member.setUserpswd(encode);
        member.setUsername(member.getLoginacct());
        member.setEmail(member.getEmail());
        member.setAuthstatus("0");
        member.setUsertype("0");
        member.setAccttype("2");
        System.out.println("插入数据:"+member.getLoginacct());
        memberMapper.insertSelective(member);

    }

    @Override
    public TMember login(String username, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        TMemberExample example =new TMemberExample();
        example.createCriteria().andLoginacctEqualTo(username);
        List<TMember> list = memberMapper.selectByExample(example);
        if (list != null && list.size() == 1){
            TMember member = list.get(0);
            boolean matches = encoder.matches(password, member.getUserpswd());
            return matches?member:null;
        }
        return null;
    }

    @Override
    public TMember findTmemberById(Integer id) {
        return memberMapper.selectByPrimaryKey(id);
    }
}
