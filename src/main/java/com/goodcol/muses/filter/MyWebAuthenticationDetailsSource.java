package com.goodcol.muses.filter;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

/**
 * 自定义detailsSource，将构建details换成自己的
 *
 * @author Zhangzp
 * @date 2023年01月12日 09:14
 */
public class MyWebAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new MyWebAuthenticationDetails(context);
    }
}