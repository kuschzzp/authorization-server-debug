package com.goodcol.muses.entity;

import lombok.Data;

import java.util.Date;

/**
 * 客户端表
 *
 * @author Mr.kusch
 * @date 2022/12/27 16:48
 */
@Data
public class OauthClient {
    /**
     * 主键ID
     */
    private String id;
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 客户端ID注册时间
     */
    private Date clientIdIssuedAt;
    /**
     * 客户端密钥
     */
    private String clientSecret;
    /**
     * 客户端密钥失效时间
     */
    private Date clientSecretExpiresAt;
    /**
     * 客户端名称
     */
    private String clientName;
    /**
     * 客户端授权方式，例如： client_secret_basic
     */
    private String clientAuthenticationMethods;
    /**
     * 该客户端可用的授权类型，例如：refresh_token,client_credentials,authorization_code
     */
    private String authorizationGrantTypes;
    /**
     * 该客户端的重定向地址：多个，分割
     */
    private String redirectUris;
    /**
     * 该客户端可用的授权范围
     */
    private String scopes;
    /**
     * 客户端配置项，长字符串
     */
    private String clientSettings;
    /**
     * 客户端的相关token配置项，长字符串
     */
    private String tokenSettings;
}