package org.example.moodvine_backend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatImageData {

    private String sessionId;

    private String userId;

    private String imageUrl;
}
