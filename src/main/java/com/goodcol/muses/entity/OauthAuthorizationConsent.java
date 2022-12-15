package com.goodcol.muses.entity;

import lombok.Data;

@Data
public class OauthAuthorizationConsent {
    private String registeredClientId;
    private String principalName;
    private String authorities;
}