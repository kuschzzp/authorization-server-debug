package com.goodcol.muses.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义密码加解密规则，就是密码后面拼个123
 *
 * @author Mr.Kusch
 * @date 2023年01月03日 15:39
 */
public class MyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString() + "123";
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(rawPassword.toString() + "123");
    }
}
