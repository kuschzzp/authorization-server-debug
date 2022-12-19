
package com.goodcol.muses.configurations;

import com.goodcol.muses.jose.Jwks;
import com.goodcol.muses.service.OidcUserInfoService;
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
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(OidcUserInfoService userInfoService) {
        return (context) -> {
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                // 这里是往 id_token中放内容
                OidcUserInfo userInfo = userInfoService.loadUser(context.getPrincipal().getName());
                context.getClaims().claims(claims -> claims.putAll(userInfo.getClaims()));
            }
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims().claims((claims) -> {
                    claims.put("ikun", "蔡徐坤");
                    claims.put("ikunnnnnn", "小黑子");
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
