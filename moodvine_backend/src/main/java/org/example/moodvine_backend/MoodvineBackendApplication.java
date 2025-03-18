package org.example.moodvine_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.moodvine_backend.mapper")
public class MoodvineBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoodvineBackendApplication.class, args);
    }

}
