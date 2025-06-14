package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.EnumValue;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public enum Gender {
    FEMALE("0"),
    MALE("1");
    @EnumValue
    private final String code;

    Gender(String code){
        this.code = code;
    }
    public String getCode(){
        return  code;
    }

    public static Gender fromCode(String code) {
        for (Gender type : Gender.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Gender code: " + code);
    }
}
