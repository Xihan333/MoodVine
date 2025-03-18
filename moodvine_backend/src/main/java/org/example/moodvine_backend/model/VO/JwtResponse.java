package org.example.moodvine_backend.model.VO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;

    private String type = "Bearer";

    private String email;



}
