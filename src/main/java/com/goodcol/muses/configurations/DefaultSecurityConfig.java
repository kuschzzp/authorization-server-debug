/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.goodcol.muses.configurations;

import com.goodcol.muses.service.MySQLUserDetailServiceImpl;
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
@EnableWebSecurity(debug = true)
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/login", "/oauth2/login").anonymous()
                                .anyRequest().authenticated()
                )
                .formLogin(configurer ->
                        configurer.loginPage("/login")
                                .loginProcessingUrl("/oauth2/login")
                )
                //这个如果你自定义的页面中没有一个csrf_token的话得配置，不然最初的过滤器都通过不了
                .csrf().disable();
        return http.build();
    }

    @Bean
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
        return new BCryptPasswordEncoder();
    }
}
