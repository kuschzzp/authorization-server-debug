
package com.goodcol.muses.configurations;

import com.goodcol.muses.entity.OauthTestUser;
import com.goodcol.muses.jose.Jwks;
import com.goodcol.muses.repository.UserRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 授权服务相关信息
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();


        authorizationServerConfigurer
                .authorizationEndpoint(authorizationEndpoint ->
                        // 自定义授权页面URL
                        authorizationEndpoint.consentPage("/oauth2/consent")
                );

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http.securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )

                //                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .csrf().disable()

                .apply(authorizationServerConfigurer)
                // 开启支持 OpenID Connect 1.0 其实就是返回值多了一个id_token
                //                .oidc(Customizer.withDefaults());

                .oidc(oidc -> {
                            //这里是往  /userinfo 接口返回值放内容
                            oidc.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userInfoMapper(
                                    oidcUserInfoAuthenticationContext -> {
                                        OAuth2AccessToken accessToken =
                                                oidcUserInfoAuthenticationContext.getAccessToken();
                                        Map<String, Object> claims = new HashMap<>();
                                        claims.put("accessToken", accessToken);
                                        claims.put("sub",
                                                oidcUserInfoAuthenticationContext.getAuthorization().getPrincipalName());
                                        return new OidcUserInfo(claims);

                                    }));
                        }
                );

        http.exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }

    /**
     * 为token的claims中增加自定义内容
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserRepository userRepository) {
        return (context) -> {
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                Optional<OauthTestUser> userByUsername
                        = userRepository.findUserByUsername(context.getPrincipal().getName());
                OidcUserInfo oidcUserInfo = userByUsername.map(oauthTestUser -> OidcUserInfo.builder()
                        .email("ikun@123.com")
                        .birthdate("0000-00-00")
                        .nickname("唱跳rap篮球")
                        .claim("ABCDEF", userByUsername.get().getAuthCodes())
                        .claim("username", oauthTestUser.getUsername())
                        .claim("ikunnnnnnnn", "张三是爱坤")
                        .claim("zhangsan--0", "张三的个人信息0")
                        .claim("zhangsan--1", "张三的个人信息1")
                        .claim("zhangsan--2", "张三的个人信息2")
                        .claim("zhangsan--3", "张三的个人信息3")
                        .claim("zhangsan--4", "张三的个人信息4")
                        .claim("zhangsan--5", "张三的个人信息5")
                        .claim("zhangsan--6", "张三的个人信息6")
                        .claim("zhangsan--7", "张三的个人信息7")
                        .claim("zhangsan--8", "张三的个人信息8")
                        .claim("zhangsan--9", "张三的个人信息9")
                        .claim("zhangsan--10", "张三的个人信息10")
                        .claim("zhangsan--11", "张三的个人信息11")
                        .claim("zhangsan--12", "张三的个人信息12")
                        .claim("zhangsan--13", "张三的个人信息13")
                        .claim("zhangsan--14", "张三的个人信息14")
                        .claim("zhangsan--15", "张三的个人信息15")
                        .claim("zhangsan--16", "张三的个人信息16")
                        .claim("zhangsan--17", "张三的个人信息17")
                        .claim("zhangsan--18", "张三的个人信息18")
                        .claim("zhangsan--19", "张三的个人信息19")
                        .build()).orElseGet(() -> new OidcUserInfo(new HashMap<>()));
                // 这里是往 id_token中放内容
                context.getClaims().claims(claims -> claims.putAll(oidcUserInfo.getClaims()));
            }
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims().claims((claims) -> {
                    claims.put("ikun", "蔡徐坤");
                    claims.put("zhangsan--0", "张三的个人信息0");
                    claims.put("zhangsan--1", "张三的个人信息1");
                    claims.put("zhangsan--2", "张三的个人信息2");
                    claims.put("zhangsan--3", "张三的个人信息3");
                    claims.put("zhangsan--4", "张三的个人信息4");
                    claims.put("zhangsan--5", "张三的个人信息5");
                    claims.put("zhangsan--6", "张三的个人信息6");
                    claims.put("zhangsan--7", "张三的个人信息7");
                    claims.put("zhangsan--8", "张三的个人信息8");
                    claims.put("zhangsan--9", "张三的个人信息9");
                    claims.put("zhangsan--10", "张三的个人信息10");
                    claims.put("zhangsan--11", "张三的个人信息11");
                    claims.put("zhangsan--12", "张三的个人信息12");
                    claims.put("zhangsan--13", "张三的个人信息13");
                    claims.put("zhangsan--14", "张三的个人信息14");
                    claims.put("zhangsan--15", "张三的个人信息15");
                    claims.put("zhangsan--16", "张三的个人信息16");
                    claims.put("zhangsan--17", "张三的个人信息17");
                    claims.put("zhangsan--18", "张三的个人信息18");
                    claims.put("zhangsan--19", "张三的个人信息19");
                });
            }
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * JwtDecoder的一个实例，用于验证访问令牌。
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * 自定jdbctemplate
     */
    @Bean
    public CustomJdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new CustomJdbcTemplate(dataSource);
    }
}
