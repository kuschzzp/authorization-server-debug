package com.goodcol.muses.sign;

import com.goodcol.muses.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的JWT解密方式
 *
 * @author Zhangzp
 * @date 2023年01月10日 16:26
 */
public class MyJwtDecoder implements JwtDecoder {

    @Value("${jwt-digitally:}")
    public String jwtDigitally;

    @Override
    public Jwt decode(String token) throws JwtException {
        SecretKey key = JwtUtils.generateKey(JwtUtils.JWT_ALG, jwtDigitally);
        // 解析 JWT 字符串
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        JwsHeader header = claimsJws.getHeader();
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", header.getAlgorithm());

        Claims body = claimsJws.getBody();

        //这里其实就是你自己解密你自己的token，然后还是构造Jwt对象，把你解析出来的几个字段补全就行，为什么要补全？肯定有地方get呗
        return new Jwt(token, body.getIssuedAt().toInstant(), body.getExpiration().toInstant(), headerMap, body);
    }
}
