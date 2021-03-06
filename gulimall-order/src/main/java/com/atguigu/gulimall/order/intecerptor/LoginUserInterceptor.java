package com.atguigu.gulimall.order.intecerptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.vo.MemberEntityResponseVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取用户登录信息相关的拦截器
 */
public class LoginUserInterceptor implements HandlerInterceptor {

   public static ThreadLocal<MemberEntityResponseVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       MemberEntityResponseVo  userVo  = (MemberEntityResponseVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        boolean match = antPathMatcher.match("/toTrade11/**", request.getRequestURI());
        boolean match1 = antPathMatcher.match("/payNotify/**", request.getRequestURI());

        if (match || match1) return  true ;
        //如果用户已经登录，直接放行
       if (userVo != null){
           threadLocal.set(userVo);
           return  true ;
       }
       // 直接返回登录的页面
        request.getSession().setAttribute("errorMsg","还未登录，请先登录");
        response.sendRedirect("http://auth.gulimal/login.html");
        return false;
    }
}
