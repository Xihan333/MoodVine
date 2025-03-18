package org.example.moodvine_backend.security.exception;


import org.example.moodvine_backend.model.VO.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(RuntimeException.class)
    public ResponseData error(Exception e){
        return ResponseData.error(401, String.valueOf(e));
    }
}
