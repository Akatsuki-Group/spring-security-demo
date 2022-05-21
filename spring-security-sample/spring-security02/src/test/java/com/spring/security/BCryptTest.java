package com.spring.security;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptTest {
    @Test
    public void test() {
        String passwd = BCrypt.hashpw("123", BCrypt.gensalt());
        System.out.println(passwd);

        boolean checkpw = BCrypt.checkpw("123", passwd);
        System.out.println(checkpw);
    }
}
