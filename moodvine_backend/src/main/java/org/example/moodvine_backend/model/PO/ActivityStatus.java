package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ActivityStatus {
    NOT_STARTED("0"),
    ONGOING("1"),
    ENDED("2");
    @EnumValue
    private final String code;

    ActivityStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}