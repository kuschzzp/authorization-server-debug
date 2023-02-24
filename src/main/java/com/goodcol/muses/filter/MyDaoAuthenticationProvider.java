//package com.goodcol.muses.filter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
///**
// * 继承验证用户信息，重写其中的认证方法
// *
// * @author Zhangzp
// * @date 2023年01月12日 09:25
// */
//@Component
//public class MyDaoAuthenticationProvider extends DaoAuthenticationProvider {
//
//    public MyDaoAuthenticationProvider() {
//    }
//
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    public MyDaoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder encoder) {
//        super.setUserDetailsService(userDetailsService);
//        super.setPasswordEncoder(encoder);
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        MyWebAuthenticationDetails details = (MyWebAuthenticationDetails) authentication.getDetails();
//        //获取到增加的参数
//        String tenantName = details.getTenantName();
//        //获取用户名密码
//        String username = (String) authentication.getPrincipal();
//        String password = (String) authentication.getCredentials();
//        //根据上述参数查询数据库，做校验
//        //做自己的一些校验...................
//        // ......................
//        if (!"123123".equals(tenantName)) {
//            this.logger.error("----------（123123）自定义参数校验失败，参数值：" + tenantName + "---------------------");
//            throw new BadCredentialsException("自定义参数校验失败");
//        }
//        return super.authenticate(authentication);
//    }
//}
