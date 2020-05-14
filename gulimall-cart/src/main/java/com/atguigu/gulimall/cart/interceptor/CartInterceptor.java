package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberEntityResponseVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>() ;
    /**
     * 目标方法执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession() ;
        UserInfoTo userInfoTo = new UserInfoTo() ;
        MemberEntityResponseVo userVo = (MemberEntityResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (userVo != null){
            userInfoTo.setUserId(userVo.getId());
        }
        Cookie[]  cookies = request.getCookies() ;
        if (cookies != null && cookies.length>0){
            for (Cookie cookie:cookies) {
                if (CartConstant.TEMP_USER_COOKIE_NAME.equals(cookie.getName())){
                    userInfoTo.setUserKey(cookie.getValue());
                }
            }
        }
        threadLocal.set(userInfoTo);  
        return true;
    }
}
