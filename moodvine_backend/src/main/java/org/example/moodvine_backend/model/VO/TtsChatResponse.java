package org.example.moodvine_backend.model.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TtsChatResponse {
    private String answer;

    private String sessionId;

    private String text;
}