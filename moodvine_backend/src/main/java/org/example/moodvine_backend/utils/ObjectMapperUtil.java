package org.example.moodvine_backend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T str2Obj(String jsonString, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
