package org.example.moodvine_backend.model.PO;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatSession {

    private String sessionId; //会话id

    private String sessionName; //会话名称

}