package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberEntityResponseVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

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

        if (userInfoTo.getUserKey() == null){
            userInfoTo.setUserKey(UUID.randomUUID().toString().replace("-",""));
        }

        threadLocal.set(userInfoTo);  
        return true;
    }

    /**
     * 目标的方法执行之后，把user_key放在cookie中
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
      UserInfoTo userInfoTo = threadLocal.get() ;
      boolean flag = false ;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies) {
            if (CartConstant.TEMP_USER_COOKIE_NAME.equals(cookie.getName())){
                flag = true ;
            }
        }

        if (!flag){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserKey());
            cookie.setDomain("localhost");
            cookie.setMaxAge(60*60*24*30);
            response.addCookie(cookie);
        }

    }
}
