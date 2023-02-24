package com.goodcol.muses.filter;

import com.goodcol.muses.entity.OauthTestUser;
import com.goodcol.muses.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * 使用过滤器的方法增加登陆参数的校验
 *
 * @author Mr.kusch
 * @date 2023/2/24 10:14
 */
@Slf4j
public class MyAddParameterCheckFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER
            = new AntPathRequestMatcher("/oauth2/login", "POST");


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public MyAddParameterCheckFilter(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //匹配上了登陆请求
        if (DEFAULT_ANT_PATH_REQUEST_MATCHER.matches(request)) {
            Map<String, String[]> parameterMap = request.getParameterMap();
            parameterMap.forEach((k, v) ->
                    System.out.println("获取到登陆参数，开始处理操作----> " + k + " ---- " + Arrays.toString(v)));
            Optional<OauthTestUser> user =
                    userRepository.findUserByUsername(request.getParameter("username"));
            if (user.isPresent()) {
                OauthTestUser testUser = user.get();
                if (!passwordEncoder.matches(request.getParameter("password"), testUser.getPassword())) {
                    throw new PasswordException("密码错误！");
                }
            } else {
                throw new UsernameNotFoundException("用户不存在！");
            }
        }
        filterChain.doFilter(request, response);
    }
}
