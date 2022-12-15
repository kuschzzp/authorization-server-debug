package com.goodcol.muses.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OauthAuthorization {
    private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    private String authorizedScopes;
    private String attributes;
    private String state;
    private String authorizationCodeValue;
    private Date authorizationCodeIssuedAt;
    private Date authorizationCodeExpiresAt;
    private String authorizationCodeMetadata;
    private String accessTokenValue;
    private Date accessTokenIssuedAt;
    private Date accessTokenExpiresAt;
    private String accessTokenMetadata;
    private String accessTokenType;
    private String accessTokenScopes;
    private String refreshTokenValue;
    private Date refreshTokenIssuedAt;
    private Date refreshTokenExpiresAt;
    private String refreshTokenMetadata;
    private String oidcIdTokenValue;
    private Date oidcIdTokenIssuedAt;
    private Date oidcIdTokenExpiresAt;
    private String oidcIdTokenMetadata;
    private String oidcIdTokenClaims;
}