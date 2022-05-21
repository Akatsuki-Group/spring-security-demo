package cn.felord.spring.security.captcha;

/**
 * The interface Captcha service.
 *
 * @author felord.cn
 * @since 13 :21
 */
public interface CaptchaService {


    /**
     * 发送验证码.
     *
     * @param phone the phone
     * @return the boolean
     */
    boolean sendCaptcha(String phone);


    /**
     * 验证码校验.
     *
     * @param phone   通过该手机号去缓存获取验证码
     * @param code the captcha code
     * @return the boolean
     */
    boolean verifyCaptcha(String phone, String code);

}
