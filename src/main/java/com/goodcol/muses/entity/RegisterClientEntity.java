package com.goodcol.muses.entity;

import lombok.Data;

/**
 * 注册客户端的参数
 */
@Data
public class RegisterClientEntity {

    private String clientId;

    private String secret;

    /**
     * 一般用不到，客户端密钥通常来说没什么有效时间
     * ！！！秒！！！
     */
    private Integer clientSecretSecondsToLive;

    /**
     * <pre>
     * 通常是 client_secret_basic
     *
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
     * authorization_code，
     * refresh_token，
     * client_credentials，
     * 一般来说，逗号分隔传上面三个值就够用了
     * <p>
     * urn:ietf:params:oauth:grant-type:jwt-bearer
     * <p>
     * 最后一个是springSecurity 5.5新增的，
     * 该模式用于当客户端希望利用一个现有的、可信任的、使用JWT语义表达的关系来获取Access Token
     */
    private String authorizationGrantTypes;

    /**
     * 逗号分隔的重定向地址
     */
    private String redirectUris;

    /**
     * 授权范围，逗号分隔
     */
    private String scopes;

    /**
     * 是否需要选择授权范围并点击
     */
    private Boolean needConsent;

    /**
     * 开启PKCE么？
     */
    private Boolean supportPkce;

    /**
     * access_token有效期，！！！秒！！！
     * 不传默认1800秒
     */
    private Integer accessTokenSecondsToLive;

    /**
     * refresh_token有效期，！！！天！！！
     * 不传默认30天
     */
    private Integer refreshTokenDaysToLive;

}
