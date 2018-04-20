package com.maotou.appservice.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.maotou.appservice.bean.RestFulBean;
import com.maotou.appservice.bean.TokenBean;
import com.maotou.appservice.dao.TokenDao;
import com.maotou.appservice.util.ResetFulUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by lichun on 18-4-16.
 */
public class ResetFulInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenDao tokenDao;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();
        HashMap<String, String> headerMaps = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            headerMaps.put(key,value);
        }
        if (!uri.endsWith(".do")){
            return true;
        }else if (uri.endsWith("user/loginbypwd.do") || uri.endsWith("update/chackupdate.do") || uri.endsWith("user/register.do")){
            return true;
        }else{
            TokenBean tokenBean = tokenDao.isTokenAvailable(headerMaps.get("phone"));
            if (tokenBean != null && tokenBean.getToken().equals(headerMaps.get("token"))){
                return true;
            }else{
                RestFulBean restFulBean = ResetFulUtil.getInstance().getRestFulBean(null, 1, "User authentication failed");
                response.setCharacterEncoding("UTF-8");
                response.encodeURL("UTF-8");
                Writer writer = response.getWriter();
                writer.write(JSONObject.toJSONString(restFulBean, SerializerFeature.WriteMapNullValue));
                return false;
            }
        }
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {}

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {}
}
