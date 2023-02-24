package com.goodcol.muses.configurations;

import com.goodcol.muses.service.MySQLUserDetailServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 基础配置
 */
@EnableWebSecurity(debug = false)
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/login", "/oauth2/login", "/oauth2/client/register")
                                .anonymous()
                                .anyRequest().authenticated()
                )
                .formLogin(configurer ->
                        configurer.loginPage("/login")
                                .loginProcessingUrl("/oauth2/login")
//                                .authenticationDetailsSource(new MyWebAuthenticationDetailsSource())
                )
                //这个如果你自定义的页面中没有一个csrf_token的话得配置，不然最初的过滤器都通过不了
                .csrf().disable();
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean
    UserDetailsService users() {
        //        UserDetails user = User.withDefaultPasswordEncoder()
        //                .username("user1")
        //                .password("password")
        //                .roles("USER")
        //                .build();
        //        return new InMemoryUserDetailsManager(user);
        return new MySQLUserDetailServiceImpl();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        //自定义的密码加密规则
        //        return new MyPasswordEncoder();
        return new BCryptPasswordEncoder();
    }
}
