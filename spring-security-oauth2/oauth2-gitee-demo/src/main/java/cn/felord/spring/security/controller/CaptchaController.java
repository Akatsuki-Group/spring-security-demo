package cn.felord.spring.security.controller;

import cn.felord.spring.security.captcha.CaptchaService;
import cn.felord.spring.security.entity.Rest;
import cn.felord.spring.security.entity.RestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * The type Captcha controller.
 *
 * @author a
 * @since 10 :56
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    CaptchaService captchaService;


    /**
     * 模拟手机号发送验证码.
     *
     * @param phone the mobile
     * @return the rest
     */
    @GetMapping("/{phone}")
    public Rest<?> captchaByMobile(@PathVariable String phone) {
        //todo 手机号 正则自行验证

        if (captchaService.sendCaptcha(phone)){
            return RestBody.ok("验证码发送成功");
        }
        return RestBody.failure(-999,"验证码发送失败");
    }

}
