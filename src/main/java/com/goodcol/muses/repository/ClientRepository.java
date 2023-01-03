package com.goodcol.muses.repository;

import com.goodcol.muses.entity.OauthClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.Optional;

/**
 * 操作 oauth_client 表的方法
 *
 * @author Mr.kusch
 * @date 2022/12/15 13:50
 */
@Slf4j
public class ClientRepository {

    @Resource
    private JdbcOperations jdbcOperations;

    public Optional<OauthClient> findByClientId(String clientId) {
        OauthClient query = jdbcOperations
                .queryForObject(
                        "select id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, " +
                                "client_name, client_authentication_methods, authorization_grant_types, " +
                                "redirect_uris, scopes, client_settings, token_settings from oauth_client where " +
                                "client_id = ?",
                        new BeanPropertyRowMapper<>(OauthClient.class),
                        clientId);
        return Optional.ofNullable(query);
    }

    public void save(OauthClient client) {
        int update = jdbcOperations.update(
                "insert into oauth_client(id, client_id, client_id_issued_at, client_secret, " +
                        "client_secret_expires_at, client_name, client_authentication_methods, " +
                        "authorization_grant_types, redirect_uris, scopes, client_settings, token_settings) values " +
                        "(?," +
                        "?,?,?,?,?,?,?,?,?,?,?)",
                client.getId(),
                client.getClientId(),
                client.getClientIdIssuedAt(),
                client.getClientSecret(),
                client.getClientSecretExpiresAt(),
                client.getClientName(),
                client.getClientAuthenticationMethods(),
                client.getAuthorizationGrantTypes(),
                client.getRedirectUris(),
                client.getScopes(),
                client.getClientSettings(),
                client.getTokenSettings());
        if (update == 0) {
            log.warn("oauth_client数据入库失败！");
        }
    }

    public void updadte(OauthClient client) {
        int update = jdbcOperations.update("UPDATE oauth_client " +
                        " SET client_id= ?," +
                        "client_id_issued_at= ?," +
                        "client_secret= ?," +
                        "client_secret_expires_at= ?," +
                        "client_name= ?," +
                        "client_authentication_methods= ?," +
                        "authorization_grant_types= ?," +
                        "redirect_uris= ?," +
                        "scopes= ?," +
                        "client_settings= ?," +
                        "token_settings= ?" +
                        "WHERE ID = ?",
                client.getClientId(),
                client.getClientIdIssuedAt(),
                client.getClientSecret(),
                client.getClientSecretExpiresAt(),
                client.getClientName(),
                client.getClientAuthenticationMethods(),
                client.getAuthorizationGrantTypes(),
                client.getRedirectUris(),
                client.getScopes(),
                client.getClientSettings(),
                client.getTokenSettings(),
                client.getId());
        if (update == 0) {
            log.warn("oauth_client数据更新入库失败！");
        }
    }

    public Optional<OauthClient> findById(String id) {
        OauthClient query = jdbcOperations
                .queryForObject(
                        "select id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, " +
                                "client_name, client_authentication_methods, authorization_grant_types, " +
                                "redirect_uris, scopes, client_settings, token_settings from oauth_client where id = ?",
                        new BeanPropertyRowMapper<>(OauthClient.class),
                        id);
        return Optional.ofNullable(query);
    }
}
