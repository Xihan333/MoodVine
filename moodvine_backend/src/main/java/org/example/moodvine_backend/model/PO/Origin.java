package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.EnumValue;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public enum Origin {
    DIARY("1"),
    CHAT("2");

    @EnumValue
    private final String code;

    Origin(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public static Origin fromCode(String code) {
        for (Origin type : Origin.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Origin code: " + code);
    }
}

