package com.goodcol.muses.service;

import com.goodcol.muses.entity.OauthTestUser;
import com.goodcol.muses.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

/**
 * 执行查找用户信息以自定义 id_token 例子
 *
 * @author Mr.kusch
 * @date 2022/12/15 13:51
 */
@Service
public class OidcUserInfoService {

    @Resource
    private UserRepository userRepository;

    public OidcUserInfo loadUser(String username) {

        Optional<OauthTestUser> userByUsername = userRepository.findUserByUsername(username);
        return userByUsername.map(oauthTestUser -> OidcUserInfo.builder()
                .email("ikun@123.com")
                .birthdate("0000-00-00")
                .nickname("唱跳rap篮球")
                .claim("ABCDEF", userByUsername.get().getAuthCodes())
                .claim("username", oauthTestUser.getUsername())
                .claim("ikunnnnnnnn", "张三是爱坤")
                .build()).orElseGet(() -> new OidcUserInfo(new HashMap<>()));
    }

}

