package com.studentsys.common.enums;

import lombok.Getter;

/**
 * 角色枚举
 */
@Getter
public enum RoleEnum {
    
    TEACHER("teacher", "教师"),
    STUDENT("student", "学生");
    
    private final String code;
    private final String name;
    
    RoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static RoleEnum getByCode(String code) {
        for (RoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
