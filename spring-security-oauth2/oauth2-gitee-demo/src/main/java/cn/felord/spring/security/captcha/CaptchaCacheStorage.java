package cn.felord.spring.security.captcha;

/**
 * 验证码缓存5分钟 一般逻辑是用户输入手机号后去获取验证码，服务端对验证码进行缓存。在最大有效期内用户只能使用验证码验证成功一次（避免验证码浪费），阅后即焚；超过最大时间后失效
 *
 * 我们可以借助很多缓存中间件来实现，这里只定义抽象方便缓存底层的切换
 *
 * @author felord.cn
 * @since 11:03
 **/
public interface CaptchaCacheStorage {

    /**
     * 验证码放入缓存.
     *
     * @param phone the phone
     * @return the string
     */
    String put(String phone);

    /**
     * 从缓存取验证码.
     *
     * @param phone the phone
     * @return the string
     */
    String get(String phone);

    /**
     * 验证码手动过期.
     *
     * @param phone the phone
     */
    void expire(String phone);
}
