package org.example.moodvine_backend.controller;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.moodvine_backend.cache.RedisChatMemory;
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


    private final ChatClient chatClient;
    private final RedisChatMemory redisChatMemory;
    private final ChatService chatService;


    @PostMapping("/hello")
    public ResponseData hello() {
        System.out.println("------hello, user-------");
        return new ResponseData(200, null, "ok");
    }

    @Operation(summary = "流式回答聊天")
    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message") String message, @RequestParam String sessionId, @RequestParam String userId) {
        Assert.notNull(message, "message不能为空");
        Assert.notNull(userId, "userId不能为空");
        // 默认生成一个会话
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            ChatSession chatSession = new ChatSession().setSessionId(sessionId).setSessionName(message.length() >= 15 ? message.substring(0, 15) : message);
            chatService.saveSession(chatSession, userId);
        }
        String finalSessionId = sessionId;
        return chatClient.prompt().user(message).advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalSessionId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)).stream().chatResponse();
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/ai/messages")
    public ResponseData getMessages(@RequestParam String sessionId) {
        Assert.notNull(sessionId, "sessionId不能为空");
        List<Message> messageList = redisChatMemory.get(sessionId, 10);
        return new ResponseData(200, "ok", messageList);
    }

    @Operation(summary = "获取会话列表")
    @GetMapping("/ai/sessions")
    public ResponseData getSessions(@RequestParam String userId) {
        Assert.notNull(userId, "userId不能为空");
        List<ChatSession> chatSessionList = chatService.getSessions(userId);
        return new ResponseData(200, "ok", chatSessionList);
    }


    @Operation(summary = "普通聊天")
    @GetMapping(value = "/ai/generate")
    public ResponseData generate(@RequestParam(value = "message") String message, @RequestParam String sessionId, @RequestParam String userId) {
        Assert.notNull(message, "message不能为空");
        Assert.notNull(userId, "userId不能为空");
        // 默认生成一个会话
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            ChatSession chatSession = new ChatSession().setSessionId(sessionId).setSessionName(message.length() >= 15 ? message.substring(0, 15) : message);
            chatService.saveSession(chatSession, userId);
        }
        String finalSessionId = sessionId;
        String answer = chatClient.prompt().user(message).advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalSessionId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)).call().content();
        return new ResponseData(200, "ok", answer);
    }

}
