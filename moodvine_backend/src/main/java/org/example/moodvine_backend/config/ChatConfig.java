package org.example.moodvine_backend.config;


import org.example.moodvine_backend.cache.RedisChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, RedisChatMemory redisChatMemory) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你叫小蔓，是一名专业的情绪疗愈助手，具备心理学和心理咨询背景，能以温暖、共情、非评判的方式帮助用户缓解情绪压力或心理困扰，你很有礼貌，回答问题条理清晰。你的首选语言是中文。")
                .defaultAdvisors(new MessageChatMemoryAdvisor(redisChatMemory))
                .build();
    }

}
