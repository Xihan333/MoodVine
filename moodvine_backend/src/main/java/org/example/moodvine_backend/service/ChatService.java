package org.example.moodvine_backend.service;


import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.moodvine_backend.cache.RedisChatMemory;
import org.example.moodvine_backend.model.DTO.ChatData;
import org.example.moodvine_backend.model.PO.ChatSession;
import org.example.moodvine_backend.model.VO.ChatResponse;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@RequiredArgsConstructor
public class ChatService {
    public static final String CHAT_SESSION_PREFIX = "chat_session:";

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisChatMemory redisChatMemory;
    private final ChatClient chatClient;

    public void saveSession(ChatSession chatSession, String userId) {
        String key = CHAT_SESSION_PREFIX + userId;
        stringRedisTemplate.opsForList().leftPush(key, JSON.toJSONString(chatSession));
    }

    public ResponseData getSessions(String userId) {
        String key = CHAT_SESSION_PREFIX + userId;
        List<String> strings = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (strings != null) {
            List<ChatSession> list = strings.stream().map(s -> JSON.parseObject(s, ChatSession.class)).toList();
            return new ResponseData(200, "ok", list);
        }
        List<ChatSession> list = List.of();
        return new ResponseData(200, "ok", list);
    }

    public ResponseData getMessages(String sessionId) {
        List<Message> messageList = redisChatMemory.get(sessionId, 10);
        return new ResponseData(200, "ok", messageList);
    }

    public ResponseData generate(ChatData chatData) {
        String sessionId = chatData.getSessionId();
        String message = chatData.getMessage();
        String userId = chatData.getUserId();

        // 默认生成一个会话
        if (!StringUtils.hasText(sessionId)) {
            sessionId = UUID.randomUUID().toString();
            ChatSession chatSession = new ChatSession().setSessionId(sessionId).setSessionName(message.length() >= 15 ? message.substring(0, 15) : message);
            saveSession(chatSession, userId);
        }
        String finalSessionId = sessionId;
        String answer = chatClient.prompt().user(message).advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalSessionId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)).call().content();
        ChatResponse response = new ChatResponse(answer, finalSessionId);
        return new ResponseData(200, "ok", response);
    }
}
