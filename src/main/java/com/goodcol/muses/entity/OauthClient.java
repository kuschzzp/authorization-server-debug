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
     * <pre>
     * 客户端认证的方式：
     * 1. client_secret_basic
     *     通常情况下都是用这种：添加Authorization header,值为client_id:client_secret Base64编码后的值
     * 2. client_secret_post
     *      在body添加grant_type=client_credentials、client_id=clientId、client_secret=clientSecret
     * 3. client_secret_jwt
     *    使用客户端信息生成一个jwt_token，client_assertion=jwt_token、client_assertion_type='urn:ietf:params:oauth:client-assertion-type:jwt-bearer'
     * 4. private_key_jwt
     *    在client_secret_jwt的基础上使用一个非对称秘钥对生成的jwt进行加密。
     * 5. none
     *    无
     * </pre>
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