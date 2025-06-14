package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum MoodType {
    POWER("1", "愉悦"),
    PEACE("2", "平静"),
    SAD("3", "难过"),
    SCARED("4", "焦虑"),
    MAD("5", "愤怒");

    @EnumValue
    private final String code; 
    private final String description; 

    MoodType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MoodType fromCode(String code) {
        for (MoodType type : MoodType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid MoodType code: " + code);
    }
} 