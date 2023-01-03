package com.goodcol.muses.repository;

import com.goodcol.muses.entity.OauthAuthorizationConsent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.Optional;

/**
 * 操作 oauth_authorization_consent 表的方法
 *
 * @author Mr.kusch
 * @date 2022/12/15 13:50
 */
@Slf4j
public class AuthorizationConsentRepository {

    @Resource
    private JdbcOperations jdbcOperations;

    public Optional<OauthAuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId,
                                                                                        String principalName) {
        OauthAuthorizationConsent consent = jdbcOperations.queryForObject("select registered_client_id, " +
                        "principal_name," +
                        " authorities from " +
                        "oauth_authorization_consent where registered_client_id=? and " +
                        "principal_name = ?",
                new BeanPropertyRowMapper<>(OauthAuthorizationConsent.class),
                registeredClientId, principalName);
        return Optional.ofNullable(consent);
    }

    public void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName) {
        int update = jdbcOperations.update("delete from oauth_authorization_consent where registered_client_id = ? " +
                "and " +
                "principal_name = ?", registeredClientId, principalName);
        if (update == 0) {
            log.warn("oauth_authorization_consent--》{} ++++++ {} 数据删除失败！", registeredClientId, principalName);
        }
    }

    public void save(OauthAuthorizationConsent consent) {
        int update = jdbcOperations.update("insert into oauth_authorization_consent (registered_client_id, " +
                        "principal_name, " +
                        "authorities) values (?,?,?)", consent.getRegisteredClientId(), consent.getPrincipalName(),
                consent.getAuthorities());
        if (update == 0) {
            log.warn("oauth_authorization_consent 数据入库失败！");
        }
    }

    public void update(OauthAuthorizationConsent consent) {
        int update = jdbcOperations.update("update oauth_authorization_consent set authorities = ? where " +
                        "registered_client_id = ? and principal_name = ?", consent.getAuthorities(),
                consent.getRegisteredClientId(),
                consent.getPrincipalName());
        if (update == 0) {
            log.warn("oauth_authorization_consent 数据更新入库失败！");
        }
    }
}
