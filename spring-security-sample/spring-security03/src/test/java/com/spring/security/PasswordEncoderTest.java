package com.spring.security;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @author tian
 */
public class PasswordEncoderTest {

    @Test
    public void test(){
        String passwd = BCrypt.hashpw("123456",BCrypt.gensalt());
        System.out.println(passwd);

        boolean checkpw = BCrypt.checkpw("123456", passwd);
        System.out.println(checkpw);
    }
}
