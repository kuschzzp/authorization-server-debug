package com.goodcol.muses.entity;

import lombok.Data;

import java.util.Date;

/**
 * 授权信息实体
 *
 * @author Mr.kusch
 * @date 2022/12/27 16:33
 */
@Data
public class OauthAuthorization {
    /**
     * 主键
     */
    private String id;
    /**
     * 注册的客户端ID
     */
    private String registeredClientId;
    /**
     * 客户端名称
     */
    private String principalName;
    /**
     * 授权类型
     */
    private String authorizationGrantType;
    /**
     * 可授权范围
     */
    private String authorizedScopes;
    /**
     * 信息汇总的长文本项
     */
    private String attributes;
    /**
     * 防csrf参数
     */
    private String state;
    /**
     * 授权码的值
     */
    private String authorizationCodeValue;
    /**
     * 授权码的颁发时间
     */
    private Date authorizationCodeIssuedAt;
    /**
     * 授权码的失效时间
     */
    private Date authorizationCodeExpiresAt;
    /**
     * 目前只看到字段中存了有效标记 例如
     * {"@class":"java.util.Collections$UnmodifiableMap","metadata.token.invalidated":false}
     */
    private String authorizationCodeMetadata;
    /**
     * access_token的值
     */
    private String accessTokenValue;
    /**
     * access_token的颁发时间
     */
    private Date accessTokenIssuedAt;
    /**
     * access_token的失效时间
     */
    private Date accessTokenExpiresAt;
    /**
     * access_token的原数据
     */
    private String accessTokenMetadata;
    /**
     * access_token的类型，暂时没见到有啥用
     */
    private String accessTokenType;
    /**
     * access_token的授权范围
     */
    private String accessTokenScopes;
    /**
     * refresh_token的值
     */
    private String refreshTokenValue;
    /**
     * refresh_token的颁发时间
     */
    private Date refreshTokenIssuedAt;
    /**
     * refresh_token的失效时间
     */
    private Date refreshTokenExpiresAt;
    /**
     * refresh_token的原数据
     */
    private String refreshTokenMetadata;
    /**
     * id_token的值
     */
    private String oidcIdTokenValue;
    /**
     * id_token的颁发时间
     */
    private Date oidcIdTokenIssuedAt;
    /**
     * id_token的失效时间
     */
    private Date oidcIdTokenExpiresAt;
    /**
     * id_token的原数据
     */
    private String oidcIdTokenMetadata;
    /**
     * id_token的附加内容
     */
    private String oidcIdTokenClaims;
}