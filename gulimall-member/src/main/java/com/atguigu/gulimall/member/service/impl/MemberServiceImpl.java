package com.atguigu.gulimall.member.service.impl;

import com.atguigu.gulimall.member.exception.PhoneRepeatException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
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


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

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
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(loginVo.getPassword(),entity.getPassword())){
                return  entity ;
            }else{
                return null ;
            }
        }
    }

}