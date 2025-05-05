package org.example.moodvine_backend.controller;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.moodvine_backend.model.VO.ResponseData;
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

import java.util.List;
import java.util.Map;


import static io.lettuce.core.pubsub.PubSubOutput.Type.message;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;  // 会话key，用于区分会话
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY; // 发送给聊天模型的上下文长度


@RestController
@RequestMapping("/chat")
public class ChatController {

    private final OllamaChatModel ollamaChatModel;
    private final ChatClient chatClient;
    private final InMemoryChatMemory chatMemory = new InMemoryChatMemory();


    public ChatController(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
        this.chatClient = ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一个生活助手，乐于帮助人解决问题，无论问什么都要礼貌回答，遇到代码问题一律回复不知道。")
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)).build();

    }

    @PostMapping("/hello")
    public ResponseData hello() {
        System.out.println("------hello, user-------");
        return new ResponseData(200, null, "ok");
    }


    @Operation(summary = "普通聊天")
    @GetMapping("/ai/generate")
    public ResponseEntity<String> generate(@RequestParam(value = "message", defaultValue = "讲个笑话") String message, @RequestParam String sessionId) {
        return ResponseEntity.ok(chatClient.prompt().user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content());
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/ai/messages")
    public List<Message> getMessages(@RequestParam String sessionId) {
        return chatMemory.get(sessionId, 10);
    }

    @Operation(summary = "流式回答聊天")
    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "讲个笑话") String message, @RequestParam String sessionId) {
        return ollamaChatModel.stream(new Prompt(new UserMessage(message)));
    }




}
