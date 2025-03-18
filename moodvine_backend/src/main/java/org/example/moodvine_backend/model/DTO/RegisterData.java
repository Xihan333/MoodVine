package org.example.moodvine_backend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterData {

    private String email;

    private String nickName;

    private String mailCode;

    private String password;
}
