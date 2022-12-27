package com.goodcol.muses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodcol.muses.entity.OauthAuthorization;
import com.goodcol.muses.entity.OauthTestUser;
import com.goodcol.muses.repository.AuthorizationRepository;
import com.goodcol.muses.repository.ClientRepository;
import com.goodcol.muses.repository.UserRepository;
import com.goodcol.muses.service.MysqlRegisteredClientRepositoryImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class MusesServiceOauth2ApplicationTests {

    @Resource
    private JdbcOperations jdbcOperations;

    @Resource
    private ClientRepository clientRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private AuthorizationRepository authorizationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void test() {

        Optional<OauthAuthorization> byAccessTokenValue = authorizationRepository.findByAccessTokenValue(
                "eyJraWQiOiI0MTdhMTA0Mi04ODVlLTQwY2ItYjJlMS1hNDc5OTU0ZjdkN2MiLCJhbGciOiJSUzI1NiJ9" +
                        ".eyJzdWIiOiJ1c2VyMSIsImF1ZCI6Im1lc3NhZ2luZy1jbGllbnQiLCJuYmYiOjE2NzExNTc3MDcsImlzcyI6Imh0dHA6Ly8xMjcuMC4wLjE6ODU1NSIsImFjY2Vzc3p6cHp6cHp6cCI6ImFjY2Vzc3p6cHp6cHp6cCIsIndhbmd3dSI6Indhbmd3dXdhbmd3dXdhbmd3dSIsImV4cCI6MTY3MTE1OTUwNywiaWF0IjoxNjcxMTU3NzA3fQ.g91krNKTD0B0CaxJ1LII80y9LNAaY0LohVAbCPLgSIBExadb0HElxaRmMPOxu9VToiQqhRA7ebXi5XueNkQfy5cEQXzRRnZ-ACmBoWFRosKy-kDaK0lPq16lhsVXsPt36LGkiJO08RKbwx_o089UTudaybcqkM-tOpwqoPJa52R0CoNI3KMhXPlasbWMUHdX7Vb40BSiu7J8f9VdXYkQqHwR5XZFLm905PGPP2Vg56V0P8Vu_iJIJKEjYR0stw1Wf_-Cz7YCg11nh-nFdCo-uxyX2QTM5F2t34FiwAS_OrmSwJLQzviGZ-L_ywOOb9D4kr6KwwSRwxt498kLkXhRAA");

        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(objectMapper.writeValueAsString(byAccessTokenValue.get()),
                    new TypeReference<Map<String, Object>>() {
                    });
            System.out.println(map.get("oidcIdTokenClaims"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟向数据库注册信息
     */
    @Test
    void contextLoads() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        /*
         * 内置的客户端注册端点
         * org.springframework.security.oauth2.server.authorization.oidc.web.OidcClientRegistrationEndpointFilter
         */
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
//                .clientSecret("{noop}secret")
                .clientSecret(bCryptPasswordEncoder.encode("secret"))
                .clientIdIssuedAt(Instant.now())
                .clientSecretExpiresAt(null)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                //支持的授权类型
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                //可以配置多个重定向地址
                .redirectUri("http://127.0.0.1:8555/test/test1")
                .redirectUri("http://127.0.0.1:8555/hahahaha")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("message.read")
                .scope("message.write")
                .scope("niubi666")
                .clientSettings(
                        ClientSettings.builder()
                                .requireAuthorizationConsent(true)
                                .requireProofKey(false)
                                .build()
                ).tokenSettings(
                        TokenSettings.builder()
                                //token有效期
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
        System.out.println("新建注册的客户端信息成功======================================");

        OauthTestUser testUser = new OauthTestUser();
        testUser.setUsername("zhangsan");
//        testUser.setPassword("{noop}123123");
        testUser.setPassword(bCryptPasswordEncoder.encode("123123"));
        testUser.setAuthCodes("A,B,C,D,E,F");

        Optional<OauthTestUser> zhangsan = userRepository.findUserByUsername("zhangsan");
        if (zhangsan.isPresent()) {
            throw new RuntimeException("用户zhangsan已存在！");
        }
        userRepository.save(testUser);
        System.out.println("新建用户 zhangsan----123123 成功======================================");

    }


    @Test
    public void en() {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String encode = bCryptPasswordEncoder.encode("123123");

        boolean matches = bCryptPasswordEncoder.matches("123123", encode);

        System.out.println("123123加密后：" + encode);
        System.out.println("匹配：" + matches);

    }


}
