package com.myweb.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.google.code.kaptcha.Producer;
import com.myweb.dto.LoginDTO;
import com.myweb.dto.LoginVO;
import com.myweb.entity.Student;
import com.myweb.entity.Teacher;
import com.myweb.service.AuthService;
import com.myweb.service.StudentService;
import com.myweb.service.TeacherService;
import com.myweb.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证Service实现类
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private Producer captchaProducer;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // 验证码缓存（生产环境建议使用Redis）
    private final Map<String, String> captchaCache = new ConcurrentHashMap<>();
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 验证验证码
        if (!verifyCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptcha())) {
            throw new RuntimeException("验证码错误");
        }
        
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        
        // 先尝试从教师表查询
        Teacher teacher = teacherService.getByUsername(username);
        if (teacher != null) {
            if (!passwordEncoder.matches(password, teacher.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            
            // 生成JWT
            String token = jwtUtil.generateToken(teacher.getId(), teacher.getUsername(), teacher.getRole());
            
            LoginVO vo = new LoginVO();
            vo.setToken(token);
            vo.setUserId(teacher.getId());
            vo.setUsername(teacher.getUsername());
            vo.setName(teacher.getName());
            vo.setRole(teacher.getRole());
            vo.setAvatar(teacher.getAvatar());
            
            return vo;
        }
        
        // 从学生表查询
        Student student = studentService.getByUsername(username);
        if (student != null) {
            if (!passwordEncoder.matches(password, student.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            
            // 生成JWT
            String token = jwtUtil.generateToken(student.getId(), student.getUsername(), student.getRole());
            
            LoginVO vo = new LoginVO();
            vo.setToken(token);
            vo.setUserId(student.getId());
            vo.setUsername(student.getUsername());
            vo.setName(student.getName());
            vo.setRole(student.getRole());
            vo.setAvatar(student.getAvatar());
            
            return vo;
        }
        
        throw new RuntimeException("用户不存在");
    }
    
    @Override
    public String[] generateCaptcha() {
        // 生成验证码文本
        String captchaText = captchaProducer.createText();
        
        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(captchaText);
        
        // 生成验证码key
        String captchaKey = IdUtil.simpleUUID();
        
        // 缓存验证码（5分钟有效）
        captchaCache.put(captchaKey, captchaText.toLowerCase());
        
        // 图片转Base64
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            String base64 = "data:image/jpg;base64," + Base64.encode(outputStream.toByteArray());
            
            return new String[]{captchaKey, base64};
        } catch (Exception e) {
            throw new RuntimeException("验证码生成失败");
        }
    }
    
    @Override
    public boolean verifyCaptcha(String key, String code) {
        String cachedCode = captchaCache.get(key);
        if (cachedCode == null) {
            return false;
        }
        
        // 验证后删除
        captchaCache.remove(key);
        
        return cachedCode.equalsIgnoreCase(code);
    }
}
