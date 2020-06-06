package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberEntityResponseVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
@Component
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
        //获取session
        HttpSession session = request.getSession() ;
        UserInfoTo userInfoTo = new UserInfoTo() ;
        //从session中获取登录的对象
        MemberEntityResponseVo userVo = (MemberEntityResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (userVo != null){
            //不为空设置userId
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
        //如果是未登录的用户，设置user_key,针对浏览器
        if (userInfoTo.getUserKey() == null){
            userInfoTo.setUserKey(UUID.randomUUID().toString().replace("-",""));
        }
       //当前的用户信息设置的threaLocal中
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
     //目标方法执行完毕后，如果不存在user_key的cookie信息.设置cookien的name为user_key的值
      UserInfoTo userInfoTo = threadLocal.get() ;
      boolean flag = false ;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies) {
            if (CartConstant.TEMP_USER_COOKIE_NAME.equals(cookie.getName())){
                flag = true ;
            }
        }
        if (!flag){
            //如果没有user_key的cookie，设置相应的user_cookie
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserKey());
            cookie.setDomain("gulimal.com");
            cookie.setMaxAge(60*60*24*30); //一个月
            response.addCookie(cookie);
        }

    }
}
