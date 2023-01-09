package com.goodcol.muses.filter;

import com.goodcol.muses.entity.RegisterClientEntity;
import com.goodcol.muses.utils.JsonUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * 客户端注册EndPoint
 *
 * @author Mr.kusch
 * @date 2023/1/6 14:06
 */
@Component
public class ClientRegistrationEndpointFilter extends OncePerRequestFilter {

    private static final String DEFAULT_OIDC_CLIENT_REGISTRATION_ENDPOINT_URI = "/oauth2/client/register";
    private final RequestMatcher clientRegistrationEndpointMatcher;

    @Resource
    private RegisteredClientRepository registeredClientRepository;
    @Resource
    private PasswordEncoder passwordEncoder;

    public ClientRegistrationEndpointFilter() {
        this(DEFAULT_OIDC_CLIENT_REGISTRATION_ENDPOINT_URI);
    }

    public ClientRegistrationEndpointFilter(String clientRegistrationEndpointUri) {
        Assert.hasText(clientRegistrationEndpointUri, "clientRegistrationEndpointUri cannot be empty");
        this.clientRegistrationEndpointMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher(clientRegistrationEndpointUri, HttpMethod.POST.name()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!this.clientRegistrationEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        RegisterClientEntity entity = toEntity(request.getParameterMap());
        if (StringUtils.isBlank(entity.getClientId())) {
            sendResponse(response, HttpStatus.BAD_REQUEST.value(), "0", "客户端ID不可为空");
            return;
        }
        if (StringUtils.isBlank(entity.getSecret())) {
            sendResponse(response, HttpStatus.BAD_REQUEST.value(), "0", "密钥不可为空");
            return;
        }
        if (StringUtils.isBlank(entity.getRedirectUris())) {
            sendResponse(response, HttpStatus.BAD_REQUEST.value(), "0", "重定向地址不可为空");
            return;
        }

        Set<ClientAuthenticationMethod> set = modifyAuthenticationMethods(entity.getClientAuthenticationMethods());
        Set<AuthorizationGrantType> grantTypes = modifyGrantTypes(entity.getAuthorizationGrantTypes());

        RegisteredClient byClientId = registeredClientRepository.findByClientId(entity.getClientId());
        if (byClientId != null) {
            sendResponse(response, HttpStatus.BAD_REQUEST.value(), "0", "客户端ID已存在！");
            return;
        }
        Instant now = Instant.now();
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(entity.getClientId())
                .clientSecret(passwordEncoder.encode(entity.getSecret()))
                .clientIdIssuedAt(now)
                .clientSecretExpiresAt(entity.getClientSecretSecondsToLive() == null ? null :
                        now.plusSeconds(entity.getClientSecretSecondsToLive()))
                .clientAuthenticationMethods(item -> item.addAll(set))
                .authorizationGrantTypes(item -> item.addAll(grantTypes))
                .redirectUris(item -> item.addAll(List.of(entity.getRedirectUris().split(","))))
                .scopes(item -> {
                    item.add(OidcScopes.OPENID);
                    item.addAll(List.of(entity.getScopes().split(",")));
                })
                .clientSettings(
                        ClientSettings.builder()
                                .requireAuthorizationConsent(null == entity.getNeedConsent() || entity.getNeedConsent())
                                .requireProofKey(null == entity.getSupportPkce() || entity.getSupportPkce())
                                .build()
                ).tokenSettings(
                        TokenSettings.builder()
                                //token有效期
                                .accessTokenTimeToLive(Duration.ofSeconds(null == entity.getAccessTokenSecondsToLive() ?
                                        1800 : entity.getAccessTokenSecondsToLive()))
                                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                                .refreshTokenTimeToLive(Duration.ofDays(null == entity.getRefreshTokenDaysToLive() ?
                                        30 : entity.getRefreshTokenDaysToLive()))
                                .build())
                .build();
        registeredClientRepository.save(registeredClient);
        sendResponse(response, HttpStatus.OK.value(), "1", "success");
    }

    private RegisterClientEntity toEntity(Map<String, String[]> parameterMap) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().length > 0 ? entry.getValue()[0] : null);
        }
        return JsonUtils.jsonToBean(JsonUtils.jsonString(map), RegisterClientEntity.class);
    }

    /**
     * 处理authorizationGrantType
     *
     * @param authorizationGrantTypes
     * @return
     */
    private Set<AuthorizationGrantType> modifyGrantTypes(String authorizationGrantTypes) {
        Set<AuthorizationGrantType> set = new HashSet<>();
        if (StringUtils.isNotBlank(authorizationGrantTypes)) {
            //不为空，就拆分设置值
            String[] list = StringUtils.split(authorizationGrantTypes, ",");
            for (String methodItem : list) {
                if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(methodItem)) {
                    set.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                }
                if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(methodItem)) {
                    set.add(AuthorizationGrantType.REFRESH_TOKEN);
                }
                if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(methodItem)) {
                    set.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                }
                if (AuthorizationGrantType.PASSWORD.getValue().equals(methodItem)) {
                    set.add(AuthorizationGrantType.PASSWORD);
                }
                if (AuthorizationGrantType.JWT_BEARER.getValue().equals(methodItem)) {
                    set.add(AuthorizationGrantType.JWT_BEARER);
                }
            }
        } else {
            //为空设置默认值
            set.add(AuthorizationGrantType.AUTHORIZATION_CODE);
            set.add(AuthorizationGrantType.REFRESH_TOKEN);
            set.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
        }
        return set;
    }

    /**
     * 处理ClientAuthenticationMethod
     *
     * @param clientAuthenticationMethods
     * @return
     */
    private Set<ClientAuthenticationMethod> modifyAuthenticationMethods(String clientAuthenticationMethods) {
        Set<ClientAuthenticationMethod> set = new HashSet<>();
        if (StringUtils.isNotBlank(clientAuthenticationMethods)) {
            //不为空，就拆分设置值
            String[] list = StringUtils.split(clientAuthenticationMethods, ",");
            for (String methodItem : list) {
                if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(methodItem)) {
                    set.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                }
                if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(methodItem)) {
                    set.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                }
                if (ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue().equals(methodItem)) {
                    set.add(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
                }
                if (ClientAuthenticationMethod.PRIVATE_KEY_JWT.getValue().equals(methodItem)) {
                    set.add(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
                }
                if (ClientAuthenticationMethod.NONE.getValue().equals(methodItem)) {
                    set.add(ClientAuthenticationMethod.NONE);
                }
            }
        } else {
            //为空设置默认值
            set.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        }
        return set;
    }

    private void sendResponse(HttpServletResponse response, Integer statusCode, String code, String message) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(statusCode);
        response.getWriter().write(JsonUtils.jsonString(map));
    }

}
