package org.example.moodvine_backend.controller;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.moodvine_backend.annotation.CustomParam;
import org.example.moodvine_backend.cache.RedisChatMemory;
import org.example.moodvine_backend.model.DTO.ChatData;
import org.example.moodvine_backend.model.DTO.ChatImageData;
import org.example.moodvine_backend.model.DTO.ChatVoiceData;
import org.example.moodvine_backend.model.PO.ChatSession;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import reactor.core.publisher.Flux;
import org.springframework.util.Assert;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import static io.lettuce.core.pubsub.PubSubOutput.Type.message;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;  // 会话key，用于区分会话
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY; // 发送给聊天模型的上下文长度


@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @PostMapping("/hello")
    public ResponseData hello() {
        System.out.println("------hello, user-------");
        return new ResponseData(200, "ok", "hello");
    }

    @Operation(summary = "流式回答聊天")
    @PostMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(@RequestBody ChatData chatData) {
        return chatService.generateStream(chatData);
    }

    @Operation(summary = "获取聊天记录")
    @PostMapping("/ai/messages")
    public ResponseData getMessages(@CustomParam String sessionId) {
        return chatService.getMessages(sessionId);
    }

    @Operation(summary = "获取会话列表")
    @PostMapping("/ai/sessions")
    public ResponseData getSessions(@CustomParam String userId) {
        return chatService.getSessions(userId);
    }

    @Operation(summary = "普通聊天")
    @PostMapping(value = "/ai/generate")
    public ResponseData generate(@RequestBody ChatData chatData) {
        return chatService.generate(chatData);
    }

    @Operation(summary = "分析图片")
    @PostMapping("/analyseImage")
    public ResponseData analyseImage(@RequestBody ChatImageData chatImageData) {
        return chatService.analyseImage(chatImageData);
    }

    @Operation(summary = "分析语音")
    @PostMapping("/analyseVoice")
    public ResponseData analyseVoice(@RequestBody ChatVoiceData chatVoiceData) {
        return chatService.analyseVoice(chatVoiceData);
    }

    @Operation(summary = "语音普通聊天")
    @PostMapping(value = "/ttsChat")
    public ResponseData ttsChat(@RequestBody ChatData chatData) throws Exception {
        return chatService.ttsChat(chatData);
    }

    @Operation(summary = "语音图片聊天")
    @PostMapping("/ttsImageChat")
    public ResponseData ttsImageChat(@RequestBody ChatImageData chatImageData) throws Exception {
        return chatService.ttsImageChat(chatImageData);
    }

    @Operation(summary = "语音语音聊天")
    @PostMapping("/ttsVoiceChat")
    public ResponseData ttsVoiceChat(@RequestBody ChatVoiceData chatVoiceData) throws Exception{
        return chatService.ttsVoiceChat(chatVoiceData);
    }


}
