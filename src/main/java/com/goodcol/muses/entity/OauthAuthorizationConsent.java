package com.goodcol.muses.entity;

import lombok.Data;

/**
 * 客户端已授权信息表
 *
 * @author Mr.kusch
 * @date 2022/12/27 16:48
 */
@Data
public class OauthAuthorizationConsent {
    /**
     * 注册的客户端ID
     */
    private String registeredClientId;
    /**
     * 客户端中的用户名，例如 QQ授权给别人，别人这里中的就是我的QQ号
     */
    private String principalName;
    /**
     * 该用户授权的范围
     */
    private String authorities;
}