package org.example.moodvine_backend.service;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.moodvine_backend.cache.RedisChatMemory;
import org.example.moodvine_backend.model.DTO.ChatData;
import org.example.moodvine_backend.model.DTO.ChatImageData;
import org.example.moodvine_backend.model.DTO.ChatVoiceData;
import org.example.moodvine_backend.model.PO.ChatSession;
import org.example.moodvine_backend.model.VO.ChatResponse;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            ChatSession chatSession = new ChatSession()
                    .setSessionId(sessionId)
                    .setSessionName(message.length() >= 15 ? message.substring(0, 15) : message);
            saveSession(chatSession, userId);
        }
        String finalSessionId = sessionId;
        String answer = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalSessionId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();
        ChatResponse response = new ChatResponse(answer, finalSessionId);
        return new ResponseData(200, "ok", response);
    }

    public Flux<org.springframework.ai.chat.model.ChatResponse> generateStream(ChatData chatData) {
        String sessionId = chatData.getSessionId();
        String message = chatData.getMessage();
        String userId = chatData.getUserId();
        // 默认生成一个会话
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            ChatSession chatSession = new ChatSession().setSessionId(sessionId).setSessionName(message.length() >= 15 ? message.substring(0, 15) : message);
            saveSession(chatSession, userId);
        }
        String finalSessionId = sessionId;
        return chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalSessionId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .chatResponse();
    }

    private String callImageAnalysisService(String imageUrl) {
        try {
            // 构建请求URL（根据你提供的图片中的测试环境地址）
            String apiUrl = "http://222.206.4.166:5000/ai/blip2-analyze-image";

            // 构建请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("prompt", "a detailed description of this photo:");
            requestBody.put("image_url", imageUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // 关键修复

            // 发送POST请求
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 解析JSON响应，提取description字段
                JsonNode rootNode = new ObjectMapper().readTree(response.getBody());
                return rootNode.path("description").asText();
            } else {
                throw new RuntimeException("图片分析服务调用失败: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("调用图片分析服务时出错: " + e.getMessage(), e);
        }
    }


    public ResponseData analyseImage(ChatImageData chatImageData) {
        String sessionId = chatImageData.getSessionId();
        String imageUrl = chatImageData.getImageUrl();
        String userId = chatImageData.getUserId();

        String imageDescription = callImageAnalysisService(chatImageData.getImageUrl());

        System.out.println(imageDescription);

        String message = "用户发送了一张图片，内容描述如下：" + imageDescription;

        // 将图片描述作为消息传递给AI对话系统
        ChatData chatData = new ChatData(message, sessionId, userId);

        // 调用已有的generate方法获取AI回复
        return generate(chatData);

    }

//    public ResponseData analyseVoice(ChatVoiceData chatVoiceData) {
//        String api = "http://222.206.4.166:5000/ai/transcribe";
//
//
//    }
}
