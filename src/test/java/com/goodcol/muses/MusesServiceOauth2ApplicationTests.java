package com.goodcol.muses;

import com.goodcol.muses.repository.ClientRepository;
import com.goodcol.muses.service.MysqlRegisteredClientRepositoryImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@SpringBootTest
class MusesServiceOauth2ApplicationTests {

    @Resource
    private JdbcOperations jdbcOperations;

    @Resource
    private ClientRepository clientRepository;

    @Test
    void contextLoads() {
        //        System.out.println(jdbcOperations.queryForList("select * from oauth_authorization_consent"));
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret("{noop}secret")
                .clientIdIssuedAt(Instant.now())
                .clientSecretExpiresAt(null)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                //可以配置多个重定向地址
                .redirectUri("http://127.0.0.1:8998/test/test1")
                .redirectUri("http://127.0.0.1:8998/hahahaha")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("message.read")
                .scope("message.write")
                .clientSettings(
                        ClientSettings.builder()
                                .requireAuthorizationConsent(true)
                                .requireProofKey(false)
                                .build()
                ).tokenSettings(
                        TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(30))
                                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                                .refreshTokenTimeToLive(Duration.ofDays(30))
                                .build())
                .build();

        RegisteredClientRepository registeredClientRepository
                = new MysqlRegisteredClientRepositoryImpl(clientRepository);
        RegisteredClient byClientId = registeredClientRepository.findByClientId("messaging-client");
        if (byClientId != null) {
            throw new RuntimeException("客户端ID已存在！");
        }
        registeredClientRepository.save(registeredClient);

    }
}
