package com.goodcol.muses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodcol.muses.entity.OauthAuthorization;
import com.goodcol.muses.entity.OauthTestUser;
import com.goodcol.muses.repository.AuthorizationRepository;
import com.goodcol.muses.repository.ClientRepository;
import com.goodcol.muses.repository.UserRepository;
import com.goodcol.muses.service.DefaultRegisteredClientRepositoryImpl;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@SpringBootTest
class MusesServiceOauth2ApplicationTests {

    @Resource
    private JdbcOperations jdbcOperations;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private ClientRepository clientRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private AuthorizationRepository authorizationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    /**
     * 模拟向数据库注册信息
     */
    @Test
    void contextLoads() {
        /*
         * 内置的 OIDC 客户端注册端点,非客户端
         *  https://openid.net/specs/openid-connect-registration-1_0.html#RegistrationRequest
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
                = new DefaultRegisteredClientRepositoryImpl(clientRepository);
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

    /**
     * NimbusJwtDecoder 校验token是否有效的方式
     */
    @Test
    public void nimbusJwt() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri("http://127.0.0.1:8555/oauth2/jwks").build();
        org.springframework.security.oauth2.jwt.Jwt jwt = decoder.decode("eyJraWQiOiJrdXNjaHp6cCIsImFsZyI6IlJTMjU2In0" +
                ".eyJzdWIiOiJ6aGFuZ3NhbiIsImF1ZCI6Im1lc3NhZ2luZy1jbGllbnQiLCJuYmYiOjE2NzI3MTUxODgsInNjb3BlIjpbIm9wZW5pZCIsIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXSwiaXNzIjoiaHR0cDovLzEyNy4wLjAuMTo4NTU1IiwiaWt1biI6IuiUoeW-kOWdpCIsImV4cCI6MTY3MjcxNjk4OCwiaWt1bm5ubm5uIjoi5bCP6buR5a2QIiwiaWF0IjoxNjcyNzE1MTg4fQ.qvgEX0iF42ds3BoHUSGG0-ZhseWpDuoE7mDehXIxGonUXJIulB2UW0DlPUS4HLbx7v8EjYwbJwcZrL_XurcON-cF9wTSjuFJWHBtNSPK7juIsl9YX6ReALiQuPjdTVG_vLh5Hmz_hJzT3MsE0ZYhEalXFV1cSyYvgnbYQDuwX8How0Lz0rrTLxF4bTXWaEjza_ROpFRhr6Y8ha8XP32XsniGduGQOd6lJxubJ7DBiQSvIOUyy0TRYX6oa76pCTXHouyqXVWML-y_fUgBkGfREZg1ZaDzs1-AY7vfTvQi-FtDcFUSVzJxdao4WgeIUAKBCY7c8cAAF5mlkXYP0xup_7ZLYP_dI8gvNXvZ8Myb_0tqoc-3X2eyoSVVeU1OIZ1JEL2Kn7kewnagLY741GhXHAbl1oPVkeNcBUszdqq2A9TMQGg73rCI6fpcQuMx0EzKAvjCFdozYvOpm8WD8n1TCWonisPl_f5-NiAcMUa__D99PIIYfAXgfBJHOR4xuaNZqxDp70e7H6y_3Z-_mEtL6hxmnWLDNIBXn6UPZy_mE0TjuGHsb71Nfx7tga2ej9E5gEHRqFlE6Tw09BY21jAex6Vbv83m0_DOZdI28kRLYLL3qMeg8rntxwQWU23NN8coA9-k0RqTorJ5ce4uCAWFXlY0-ccZhpar51XTbUocx70");
        Map<String, Object> claims = jwt.getClaims();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            System.out.println(entry.getKey() + "----" + entry.getValue());
        }
    }


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

    @Test
    public void jjwt() {
        Date date = new Date();
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "jwt-rule")
                .setSubject("login")
                //接收jwt的一方
                .setAudience("web")
                .setId(null)
                //token颁发者
                .setIssuer("zhangsan")
                //某个时间点前无法使用
                .setNotBefore(null)
                .setIssuedAt(date)
                .setExpiration(org.apache.commons.lang3.time.DateUtils.addSeconds(date, 1800))
                .addClaims(Collections.singletonMap("666", "888"));
        String newJwt = jwtBuilder.compact();
        System.out.println(newJwt);
        Jwt parse = Jwts.parser().setSigningKey("jwt-rule").parse(newJwt);
    }


    @Test
    public void genJwtByNimbus() throws Exception {

        Calendar signTime = Calendar.getInstance();
        Date signTimeTime = signTime.getTime();
        signTime.add(Calendar.MINUTE, 10);
        Date expireTime = signTime.getTime();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("http://localhost:18080")
                .subject("userId")
                .audience(Arrays.asList("https://app-one.com", "https://app-two.com"))
                .expirationTime(expireTime)
                .notBeforeTime(signTimeTime)
                .issueTime(signTimeTime)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", "read write")
                .build();


        // 传入header 和 payload
        SignedJWT signedJWT =
                new SignedJWT(
                        new JWSHeader
                                .Builder(JWSAlgorithm.HS256)
                                .type(JOSEObjectType.JWT)
                                .build(),
                        claimsSet);

        String jwtSecret =
                "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj";

        MACSigner macSigner = new MACSigner(jwtSecret);
        MACVerifier macVerifier = new MACVerifier(jwtSecret);

        // 进行签名
        signedJWT.sign(macSigner);
        String token = signedJWT.serialize();
        System.out.println("token---->>>>" + token);

        //校验
        SignedJWT parse = SignedJWT.parse(token);
        if (!parse.verify(macVerifier)) {
            throw new RuntimeException("无效的token");
        }
        System.out.println(parse.getJWTClaimsSet().getClaims());

        // !!!!!!!! TNND  好像jjwt 无法解析 nimbus 生成的token !!!!!!!!!!!!
        Jwts.parser().setSigningKey(jwtSecret).parse(token);
    }


}
