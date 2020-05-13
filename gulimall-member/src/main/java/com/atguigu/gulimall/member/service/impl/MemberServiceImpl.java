package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gulimall.member.exception.PhoneRepeatException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
import com.atguigu.gulimall.member.vo.OauthLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private RestTemplate restTemplate ;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(MemberRegisterVo vo) {
        //判断手机号是否重复，
        int phoneCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile",vo.getPhone())) ;
        if (phoneCount > 0){
            throw  new PhoneRepeatException() ;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder() ;
        //保存用户信息
        MemberEntity entity = new MemberEntity();
        entity.setNickname(vo.getUserName());
        entity.setBirth(new Date());
        entity.setCreateTime(new Date());
        entity.setEmail("qq.com");
        entity.setMobile(vo.getPhone());
        entity.setUsername(vo.getUserName());
        entity.setPassword(encoder.encode(vo.getPassword())); //加密
        this.baseMapper.insert(entity) ;

    }

    @Override
    public MemberEntity login(MemberLoginVo loginVo) {

        //通过用户名或者手机号，查询此账号是否存在
        MemberEntity entity = this.baseMapper.selectOne(
                new QueryWrapper<MemberEntity>()
                .eq("mobile",loginVo.getUsername()).or().eq("username",loginVo.getUsername())
        ) ;
        if(entity == null){
            return  null ;
        }else{
            // 密码匹配校验
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(loginVo.getPassword(),entity.getPassword())){
                return  entity ;
            }else{
                return null ;
            }
        }
    }

    @Override
    @Transactional
    public MemberEntity oauthLogin(OauthLoginVo loginVo) {
        //根据UID查询用户是否用社交账号登录过
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid",loginVo.getUid())) ;
        //此用户已经使用社交账号登录过
        if (entity != null){
           entity.setSocialUid(loginVo.getUid());
           entity.setAccessToken(loginVo.getAccess_token());
           entity.setExpiresIn(loginVo.getExpires_in());
           this.baseMapper.updateById(entity) ;
           return  entity ;
        }else{
            MemberEntity memberEntity = new MemberEntity() ;
            //调用社交账号查询接口，查询用户的基本信息
            String response = restTemplate.getForObject("https://api.weibo.com/2/users/show.json?access_token="+loginVo.getAccess_token()+"&uid="+loginVo.getUid(), String.class);
            JSONObject jsonObject = JSON.parseObject(response);
            String name = (String)jsonObject.get("name");
            String gender = jsonObject.getString("gender") ;
            // memberEntity.setExpiresIn(lo);
            memberEntity.setUsername(name);
            memberEntity.setNickname(name);
            memberEntity.setGender("m".equals(gender)?0:1);
            memberEntity.setSocialUid(loginVo.getUid());
            memberEntity.setExpiresIn(loginVo.getExpires_in());
            memberEntity.setAccessToken(loginVo.getAccess_token());
            this.baseMapper.insert(memberEntity) ;
            return  memberEntity ;
        }

    }

}