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
import org.example.moodvine_backend.model.VO.TtsChatResponse;
import org.example.moodvine_backend.utils.MultipartFileResource;
import org.example.moodvine_backend.utils.UrlMultipartFile;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
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

    @Autowired
    @Qualifier("chatClient")
    private ChatClient chatClient;

    @Autowired
    private TtsService ttsService;

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

    private String callVoiceToTextService(MultipartFile voiceFile) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartFileResource(voiceFile));

        // 发送请求
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://222.206.4.166:5000/ai/transcribe",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 处理响应
        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode json = new ObjectMapper().readTree(response.getBody());
            return json.path("text").asText();
        } else {
            throw new RuntimeException("语音服务返回错误: " + response.getStatusCode());
        }
    }


    public ResponseData analyseImage(ChatImageData chatImageData) {
        String sessionId = chatImageData.getSessionId();
        String imageUrl = chatImageData.getImageUrl();
        String userId = chatImageData.getUserId();

        String imageDescription = callImageAnalysisService(imageUrl);

        System.out.println(imageDescription);

        String message = "用户发送了一张图片，内容描述如下：\\n" + imageDescription;

        // 将图片描述作为消息传递给AI对话系统
        ChatData chatData = new ChatData(message, sessionId, userId);

        // 调用已有的generate方法获取AI回复
        return generate(chatData);

    }

    public ResponseData analyseVoice(ChatVoiceData chatVoiceData) {
        try {
            String sessionId = chatVoiceData.getSessionId();
            String voiceUrl = chatVoiceData.getVoiceUrl();
            String userId = chatVoiceData.getUserId();

            MultipartFile voiceFile = new UrlMultipartFile(voiceUrl);

            String transcribedText = callVoiceToTextService(voiceFile);

//            System.out.println("转化后的文本内容：" + transcribedText);

            String message = "这是用户发送的语音转文字内容（英文表示语音的情绪），请根据内容直接给出回答：\\n" + transcribedText;

            ChatData chatData = new ChatData(message, sessionId, userId);

            // 调用已有的generate方法获取AI回复
            return generate(chatData);
        } catch (Exception e) {
            return new ResponseData(500, "语音分析失败: " + e.getMessage(), null);
        }
    }

    private String extractSentence(String response) {
        // 处理多行think标签（包括不规则换行）
        String cleaned = response.replaceAll("(?is)<think>.*?</think>", "").trim();

        // 处理残留的think片段（如果标签不完整）
        cleaned = cleaned.replaceAll("<?think>?", "").trim();

        // 提取第一个有效句子（如果仍有混杂内容）
        String[] lines = cleaned.split("\\r?\\n");
        for (String line : lines) {
            if (!line.isBlank() && !line.matches(".*思考.*|.*分析.*")) { // 过滤中文分析文本
                return line.trim();
            }
        }

        // 终极回退：返回原始内容首行
        return response.lines().findFirst().orElse("").trim();
    }

    public ResponseData ttsChat(ChatData chatData) throws Exception {
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

        answer = extractSentence(answer);
        String url = (String) ttsService.speech(answer).getData();

        TtsChatResponse response = new TtsChatResponse(answer, finalSessionId, url);

        return new ResponseData(200, "ok", response);
    }

    public ResponseData ttsImageChat(ChatImageData chatImageData) throws Exception {
        String sessionId = chatImageData.getSessionId();
        String imageUrl = chatImageData.getImageUrl();
        String userId = chatImageData.getUserId();

        String imageDescription = callImageAnalysisService(imageUrl);

        System.out.println(imageDescription);

        String message = "用户发送了一张图片，内容描述如下：\\n" + imageDescription;

        // 将图片描述作为消息传递给AI对话系统
        ChatData chatData = new ChatData(message, sessionId, userId);

        // 调用已有的generate方法获取AI回复
        return ttsChat(chatData);
    }


    public ResponseData ttsVoiceChat(ChatVoiceData chatVoiceData) {
        try {
            String sessionId = chatVoiceData.getSessionId();
            String voiceUrl = chatVoiceData.getVoiceUrl();
            String userId = chatVoiceData.getUserId();

            MultipartFile voiceFile = new UrlMultipartFile(voiceUrl);

            String transcribedText = callVoiceToTextService(voiceFile);

            System.out.println("转化后的文本内容：" + transcribedText);

            String message = "这是用户发送的语音转文字内容（英文表示语音的情绪），请根据内容直接给出回答：\\n" + transcribedText;

            ChatData chatData = new ChatData(message, sessionId, userId);

            // 调用已有的generate方法获取AI回复
            return ttsChat(chatData);
        } catch (Exception e) {
            return new ResponseData(500, "语音分析失败: " + e.getMessage(), null);
        }
    }
}
