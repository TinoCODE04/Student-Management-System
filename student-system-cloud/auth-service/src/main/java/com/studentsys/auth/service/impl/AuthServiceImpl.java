package com.studentsys.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.code.kaptcha.Producer;
import com.studentsys.auth.mapper.StudentMapper;
import com.studentsys.auth.mapper.TeacherMapper;
import com.studentsys.auth.service.AuthService;
import com.studentsys.common.dto.LoginDTO;
import com.studentsys.common.dto.LoginVO;
import com.studentsys.common.entity.Student;
import com.studentsys.common.entity.Teacher;
import com.studentsys.common.exception.BusinessException;
import com.studentsys.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final Producer captchaProducer;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // 验证码缓存（生产环境应使用Redis）
    private final Map<String, String> captchaCache = new ConcurrentHashMap<>();
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 验证验证码
        if (!verifyCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptcha())) {
            throw new BusinessException("验证码错误");
        }
        
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        
        // 先查询教师
        Teacher teacher = teacherMapper.selectOne(
                new LambdaQueryWrapper<Teacher>().eq(Teacher::getUsername, username));
        
        if (teacher != null) {
            // 验证密码
            if (!passwordEncoder.matches(password, teacher.getPassword())) {
                throw new BusinessException("密码错误");
            }
            
            // 生成Token
            String token = JwtUtil.generateToken(
                    teacher.getId(), teacher.getUsername(), teacher.getRole(),
                    jwtSecret, jwtExpiration);
            
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(token);
            loginVO.setUserId(teacher.getId());
            loginVO.setUsername(teacher.getUsername());
            loginVO.setName(teacher.getName());
            loginVO.setRole(teacher.getRole());
            loginVO.setAvatar(teacher.getAvatar());
            
            log.info("教师登录成功: {}", username);
            return loginVO;
        }
        
        // 查询学生
        Student student = studentMapper.selectOne(
                new LambdaQueryWrapper<Student>().eq(Student::getUsername, username));
        
        if (student != null) {
            // 检查账号状态
            if ("disabled".equals(student.getStatus())) {
                throw new BusinessException("账号已被禁用");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(password, student.getPassword())) {
                throw new BusinessException("密码错误");
            }
            
            // 生成Token
            String token = JwtUtil.generateToken(
                    student.getId(), student.getUsername(), student.getRole(),
                    jwtSecret, jwtExpiration);
            
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(token);
            loginVO.setUserId(student.getId());
            loginVO.setUsername(student.getUsername());
            loginVO.setName(student.getName());
            loginVO.setRole(student.getRole());
            loginVO.setAvatar(student.getAvatar());
            
            log.info("学生登录成功: {}", username);
            return loginVO;
        }
        
        throw new BusinessException("用户不存在");
    }
    
    @Override
    public String[] generateCaptcha() {
        // 生成验证码文本
        String captchaText = captchaProducer.createText();
        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(captchaText);
        
        // 生成唯一key
        String key = UUID.randomUUID().toString();
        
        // 存储验证码（5分钟有效）
        captchaCache.put(key, captchaText.toLowerCase());
        
        // 启动定时清理（5分钟后）
        new Thread(() -> {
            try {
                Thread.sleep(5 * 60 * 1000);
                captchaCache.remove(key);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        // 转换为Base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return new String[]{key, "data:image/png;base64," + base64};
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            throw new BusinessException("生成验证码失败");
        }
    }
    
    @Override
    public boolean verifyCaptcha(String key, String code) {
        if (key == null || code == null) {
            return false;
        }
        String cached = captchaCache.get(key);
        if (cached == null) {
            return false;
        }
        // 验证后删除
        captchaCache.remove(key);
        return cached.equalsIgnoreCase(code);
    }
}
