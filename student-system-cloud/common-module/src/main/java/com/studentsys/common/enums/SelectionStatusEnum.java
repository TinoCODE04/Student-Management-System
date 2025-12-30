package com.studentsys.common.enums;

import lombok.Getter;

/**
 * 选课状态枚举
 */
@Getter
public enum SelectionStatusEnum {
    
    PENDING("pending", "待开课"),
    STUDYING("studying", "学习中"),
    COMPLETED("completed", "已完成"),
    DROPPED("dropped", "已退选");
    
    private final String code;
    private final String name;
    
    SelectionStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static SelectionStatusEnum getByCode(String code) {
        for (SelectionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 是否可以退课
     */
    public boolean canDrop() {
        return this == PENDING;
    }
}
