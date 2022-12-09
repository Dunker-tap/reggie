package com.ithema.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.ithema.reggie.common.BaseContext;
import com.ithema.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录
 */
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
@Slf4j
public class LoginFilter implements Filter {
    //创建路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //通过日志打印请求路径
        log.info("拦截到请求:{}", request.getRequestURI());

        //1.获取本次请求的URI
        String uri = request.getRequestURI();
        //1.1设置不需要进行处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",  //静态资源
                "/front/**",
        };
        //2.判断本次请求是否需要处理
        boolean check = check(urls, uri);

        //3.如果不需要处理，则直接放行
        if (check) {
            log.info("本次登录不需要处理:{}", uri);
            //经过测试，过滤器可以生效
            chain.doFilter(request, response);
            return;
        }
        //4.判断登录状态，如果已经登录，则直接放行
        Object o = request.getSession().getAttribute("employee");
        if (o != null) {
            log.info("本次登录的用户的id:{}", o);
            //用户已经登录了
            Long empId = (Long) o;
            BaseContext.setCurrentId(empId);
            
            //经过测试，过滤器可以生效
            chain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
        //5.如果未登录则返回为登录结果，通过输出流方式将字符串转换成json格式输出
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配
     */
    public boolean check(String urls[], String requestURI) {
        for (String url : urls) {
            boolean b = PATH_MATCHER.match(url, requestURI);
            if (b) return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
