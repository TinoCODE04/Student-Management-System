package com.studentsys.auth.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码配置
 */
@Configuration
public class CaptchaConfig {
    
    @Bean
    public Producer captchaProducer() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 图片宽度
        properties.setProperty("kaptcha.image.width", "120");
        // 图片高度
        properties.setProperty("kaptcha.image.height", "40");
        // 字符集
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 字符长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // 背景颜色
        properties.setProperty("kaptcha.background.clear.from", "white");
        properties.setProperty("kaptcha.background.clear.to", "white");
        // 干扰线
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        // 边框
        properties.setProperty("kaptcha.border", "no");
        
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        
        return kaptcha;
    }
}
