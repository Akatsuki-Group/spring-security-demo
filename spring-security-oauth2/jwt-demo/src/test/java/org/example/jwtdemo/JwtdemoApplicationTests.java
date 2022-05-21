package org.example.jwtdemo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JwtdemoApplicationTests {

    private static final String SECRETKEY = "admin";

    @Test
    public void test() {
        String token = getToken();
        //三部分的base64解密
        System.out.println("=========");
        String[] split = token.split("\\.");
        System.out.println(Base64Codec.BASE64.decodeToString(split[0]));
        System.out.println(Base64Codec.BASE64.decodeToString(split[1]));
        //无法解密
        System.out.println(Base64Codec.BASE64.decodeToString(split[2]));
    }

    public String getToken() {
        //创建一个JwtBuilder对象
        JwtBuilder jwtBuilder = Jwts.builder()
                //声明的标识{"jti":"666"}
                .setId("666")
                //主体，用户{"sub":"Fox"}
                .setSubject("FOX")
                //创建日期{"ita":"xxxxxx"}
                .setIssuedAt(new Date())
                //设置过期时间   1分钟
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 10000))
                //直接传入map
                // .addClaims(map)
                .claim("roles", "admin")
                .claim("logo", "xxx.jpg")
                //签名手段，参数1：算法，参数2：盐
                .signWith(SignatureAlgorithm.HS256, SECRETKEY);
        //获取token
        String token = jwtBuilder.compact();
        System.out.println(token);
        return token;
    }

    @Test
    public void testParseToken() {
        //token
        String token = getToken();
        //解析token获取载荷中的声明对象
        Claims claims = Jwts.parser()
                .setSigningKey(SECRETKEY)
                .parseClaimsJws(token)
                .getBody();

        System.out.println("id:" + claims.getId());
        System.out.println("subject:" + claims.getSubject());
        System.out.println("issuedAt:" + claims.getIssuedAt());

        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("签发时间:" + sf.format(claims.getIssuedAt()));
        System.out.println("过期时间:" + sf.format(claims.getExpiration()));
        System.out.println("当前时间:" + sf.format(new Date()));

        System.out.println("roles:" + claims.get("roles"));
        System.out.println("logo:" + claims.get("logo"));
    }

}