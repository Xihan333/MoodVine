package org.example.moodvine_backend.config;


import org.example.moodvine_backend.cache.RedisChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    // 对话专用的ChatClient
    @Bean
    @Qualifier("chatClient")
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, RedisChatMemory redisChatMemory) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你叫小漫，是一名专业的情绪疗愈助手，具备心理学和心理咨询背景，能以温暖、共情、非评判的方式帮助用户缓解情绪压力或心理困扰，你很有礼貌，回答问题条理清晰，控制字数在50字以内。你的首选语言是中文。")
                .defaultAdvisors(new MessageChatMemoryAdvisor(redisChatMemory))
                .build();
    }


//    @Bean
//    @Qualifier("chatAnalysisClient")
//    public ChatClient chatAnalysisClient(OllamaChatModel ollamaChatModel) {
//        return ChatClient.builder(ollamaChatModel)
//                .defaultSystem("请将以下对话总结为一段话，不要分点，保持自然流畅的疗愈语气，重点突出用户的核心困扰和AI建议的关键方法。要求总结中包含用户的重要觉察时刻，并保留AI疗愈师温暖支持的语调，结尾带有延续性。")
//                .build();
//    }

    // 总结聊愈记录->一句话专用的ChatClient
    @Bean
    @Qualifier("chatAnalysisClient")
    public ChatClient chatAnalysisClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("请将以下对话总结为一句话，15个字以内，要求能够总结本次对话的核心，充满温暖与鼓励")
                .build();
    }

    // 情绪分析专用的ChatClient
    @Bean
    @Qualifier("emotionAnalysisClient")
    public ChatClient emotionAnalysisClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一名专业的心理分析师，擅长从用户的文字中识别和分析情绪状态。你需要客观、专业地分析用户输入中表现出的情绪类型，情绪类型有如下几种：1.power（愉悦） 2.peace（平静） 3.sad（难过） 4.scared（焦虑） 5.mad（愤怒）。仔细阅读用户提供的日记或文字内容，分析其中表达的情绪，并输出整体的的情绪类型（如：power）。仅输出一个情绪关键词，不添加任何额外解释或描述。")
                .build();
    }

    // 用户标签分析专用的ChatClient
    @Bean
    @Qualifier("labelAnalysisClient")
    public ChatClient labelAnalysisClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一名专业的心理分析师，擅长从用户的文字中识别和分析情绪状态。你需要客观、专业地分析用户输入中表现出的用户个性标签，如：不甘平凡的奋斗者，仅输出一个用户标签，不添加任何额外解释或描述。")
                .build();
    }



//    @Bean
//    @Qualifier("emotionAnalysisClient")
//    public ChatClient emotionAnalysisClient(OllamaChatModel ollamaChatModel) {
//        return ChatClient.builder(ollamaChatModel)
//                .defaultSystem("你是一名专业的心理分析师，擅长从用户的文字中识别和分析情绪状态。你需要客观、专业地分析用户输入中表现出的情绪类型，情绪类型有如下几种：1.power（愉悦） 2.peace（平静） 3.sad（难过） 4.scared（焦虑） 5.mad（愤怒）。仔细阅读用户提供的日记或文字内容，分析其中表达的情绪，并按照出现的顺序依次标注对应的情绪类型（如：power peace sad）。仅输出情绪关键词，不添加任何额外解释或描述。")
//                .build();
//    }

    // 日记总结专用的ChatClient
//    @Bean
//    @Qualifier("diarySummaryClient")
//    public ChatClient diarySummaryClient(OllamaChatModel ollamaChatModel) {
//        return ChatClient.builder(ollamaChatModel)
//                .defaultSystem("你是一名专业的日记内容分析师，能够从用户的日记中提取关键事件、情感变化和主题。你需要用简洁的语言总结日记的主要内容，突出情感脉络和重要事件。总结控制在80字以内，以第二人称体现")
//                .build();
//    }
}
