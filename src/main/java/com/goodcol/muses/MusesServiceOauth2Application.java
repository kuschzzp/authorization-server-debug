package com.goodcol.muses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <pre>
 *
 * <a href="https://github.com/spring-projects/spring-authorization-server">https://github.com/spring-projects/spring-authorization-server</a>
 *
 * <a href="https://docs.authlib.org/en/latest/specs/rfc8628.html#authlib.oauth2.rfc8628.DeviceCodeGrant">设备授权1</a>
 *
 * <a href="https://docs.github.com/zh/developers/apps/building-oauth-apps/authorizing-oauth-apps">设备授权2</a>
 *
 * <a href="https://blog.csdn.net/dghkgjlh/article/details/121665795">jwt的使用</a>
 *
 * <a href="https://docs.spring.io/spring-authorization-server/docs/current/reference/html/guides/how-to-jpa.html#define-data-model">官方数据库文档例子</a>
 *
 * <a href="https://docs.spring.io/spring-authorization-server/docs/current/reference/html/protocol-endpoints.html#oauth2-authorization-endpoint">端点信息</a>
 *
 * <a href="https://tonyxu-io.github.io/pkce-generator/">在线生成PKCE</a>
 *
 * <a href="https://base64.us/">在线base64</a>
 *
 * <a href="https://jwt.ms/">在线解析token</a>
 *
 * <a href="https://uutool.cn/rsa-generate/">在线生成RSA</a>
 *
 * </pre>
 *
 * @author Mr.kusch
 * @date 2022/11/10 16:37
 */
@SpringBootApplication
public class MusesServiceOauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(MusesServiceOauth2Application.class, args);
    }
}
