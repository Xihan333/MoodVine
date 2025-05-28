package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.EnumValue;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public enum UserType {
    ADMIN("1"),
    USER("0");

    @EnumValue
    private final String code;
    UserType(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
