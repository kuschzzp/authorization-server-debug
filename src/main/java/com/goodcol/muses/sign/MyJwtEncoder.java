package com.goodcol.muses.sign;

import com.goodcol.muses.entity.OauthTestUser;
import com.goodcol.muses.repository.UserRepository;
import com.goodcol.muses.utils.JwtUtils;
import io.jsonwebtoken.JwtBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 自定义的 Jwt 加密规则
 *
 * @author Zhangzp
 * @date 2023年01月10日 16:26
 */
public class MyJwtEncoder implements JwtEncoder {

    private final UserRepository userRepository;

    public MyJwtEncoder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 一个加密的字符串
     */
    @Value("${jwt-digitally:}")
    public String jwtDigitally;

    @Override
    public Jwt encode(JwtEncoderParameters parameters) throws JwtEncodingException {
        Assert.notNull(parameters, "parameters cannot be null");

        JwtClaimsSet claims = parameters.getClaims();
        String subject = claims.getSubject();

        //这里可以根据用户账号数据库查询数据，填充到access_token里面
        Optional<OauthTestUser> userByUsername = userRepository.findUserByUsername(subject);
        if (userByUsername.isEmpty()) {
            throw new RuntimeException("数据库不存在该用户信息。");
        }

        OauthTestUser testUser = userByUsername.get();

        JwtBuilder jwtBuilder = JwtUtils.buildJWTBuilder(
                        JwtUtils.JWT_ALG,
                        JwtUtils.generateKey(JwtUtils.JWT_ALG, jwtDigitally),
                        "login", null, UUID.randomUUID().toString(), "OAuth2", null,
                        (int) (claims.getExpiresAt().getEpochSecond() - claims.getIssuedAt().getEpochSecond())
                )
                .claim("loginId", testUser.getUsername())
                .claim("forceResetPwd", testUser.getPassword())
                .claim("scope", parameters.getClaims().getClaim("scope"));

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", JwtUtils.JWT_ALG.getValue());

        String jws = jwtBuilder.compact();

        //其实就是你自己生成好 token 字符串，然后构造一个Jwt对象就行，生成方法随意即可
        return new Jwt(jws, claims.getIssuedAt(), claims.getExpiresAt(), headerMap, claims.getClaims());

    }
}
