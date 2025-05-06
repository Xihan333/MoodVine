package org.example.moodvine_backend.service;


import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.moodvine_backend.model.PO.ChatSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    public static final String CHAT_SESSION_PREFIX = "chat_session:";

    private final StringRedisTemplate stringRedisTemplate;


    /**
     * 保存会话
     *
     * @param chatSession 会话
     */
    public void saveSession(ChatSession chatSession, String userId) {
        String key = CHAT_SESSION_PREFIX + userId;
        stringRedisTemplate.opsForList().leftPush(key, JSON.toJSONString(chatSession));
    }

    /**
     * 获取会话列表
     *
     * @return 会话列表
     */
    public List<ChatSession> getSessions(String userId) {
        String key = CHAT_SESSION_PREFIX + userId;
        List<String> strings = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (strings != null) {
            return strings.stream().map(s -> JSON.parseObject(s, ChatSession.class)).toList();
        }
        return List.of();
    }



}
