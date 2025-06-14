package org.example.moodvine_backend.service;

import lombok.RequiredArgsConstructor;
import org.example.moodvine_backend.mapper.MoodMapper;
import org.example.moodvine_backend.mapper.ScripMapper;
import org.example.moodvine_backend.model.PO.Mood;
import org.example.moodvine_backend.model.PO.MoodType;
import org.example.moodvine_backend.model.PO.Scrip;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScripService {
    @Autowired
    private ScripMapper scripMapper;

    @Autowired
    private ChatService chatService;

    @Autowired
    @Qualifier("emotionAnalysisClient")
    private ChatClient emotionAnalysisClient;

    @Autowired
    @Qualifier("chatAnalysisClient")
    private ChatClient chatAnalysisClient;

    @Autowired
    private MoodMapper moodMapper;

    public ResponseData getAllScrips(Integer userId) {
        List<Scrip> scrips = scripMapper.getAllScrips(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        List<Map<String,Object>> formattedScrips = scrips.stream().map(scrip -> {
            Map<String,Object> map = new HashMap<>();
            map.put("id",scrip.getId());
            map.put("mood",Integer.parseInt(scrip.getMood().getCode()));
            map.put("sentence",scrip.getSentence());
            map.put("time",sdf.format(scrip.getTime()));
            return map;
        }).collect(Collectors.toList());
        return new ResponseData(200, "成功", formattedScrips);
    }

    public ResponseData getIndexScrips(Integer userId) {
        List<Scrip> scrips = scripMapper.getIndexScrips(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        List<Map<String,Object>> formattedScrips = scrips.stream().map(scrip -> {
            Map<String,Object> map = new HashMap<>();
            map.put("id",scrip.getId());
            map.put("mood",Integer.parseInt(scrip.getMood().getCode()));
            map.put("sentence",scrip.getSentence());
            map.put("time",sdf.format(scrip.getTime()));
            return map;
        }).collect(Collectors.toList());
        return new ResponseData(200, "成功", formattedScrips);
    }

    public ResponseData getScripDetail(Integer id) {
        Scrip scripDetail = scripMapper.getScripDetail(id);
        if (scripDetail == null) {
            return ResponseData.failure(400, "未找到指定的纸条");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("mood",Integer.parseInt(scripDetail.getMood().getCode()));
        map.put("sentence",scripDetail.getSentence());
        map.put("content",scripDetail.getContent());
        return new ResponseData(200, "查找成功", map);
    }

    // 从响应内容中提取情绪代码
    private String extractMoodCode(String response) {
        // 使用多行匹配模式 (?s) 和非贪婪匹配 .*?
        String cleaned = response.replaceAll("(?s)<think>.*?</think>", "").trim();

        // 如果去除标签后为空，则搜索整个响应内容
        if(cleaned.isEmpty()) {
            cleaned = response;
        }

        // 使用正则表达式匹配情绪关键词（不区分大小写）
        if(cleaned.matches("(?i).*\\bpower\\b.*")) return "1";
        if(cleaned.matches("(?i).*\\bpeace\\b.*")) return "2";
        if(cleaned.matches("(?i).*\\bsad\\b.*")) return "3";
        if(cleaned.matches("(?i).*\\bscared\\b.*")) return "4";
        if(cleaned.matches("(?i).*\\bmad\\b.*")) return "5";

        // 添加日志帮助调试
        System.out.println("无法识别的情绪响应: " + response);

        // 改为返回null或抛出异常，让调用方处理
        return null;
    }

    private String extractSentence(String response) {
        // 改进后的正则表达式，处理多行think标签
        String sentence = response.replaceAll("(?s)<think>.*?</think>", "").trim();

        // 如果处理后为空，返回原始内容
        return sentence.isEmpty() ? response : sentence;
    }

    public ResponseData saveScrip(Integer userId, String content) {
        // 使用情绪分析客户端获取情绪类型
        String moodResponse  = emotionAnalysisClient.prompt()
                .user(content)
                .call()
                .content();

        System.out.println(moodResponse );

        String moodCode = extractMoodCode(moodResponse);

        String sentenceResponse = chatAnalysisClient.prompt()
                .user(content)
                .call()
                .content();

        String sentence = extractSentence(sentenceResponse);

        System.out.println(sentenceResponse);
        System.out.println(sentence);

        MoodType moodType = MoodType.fromCode(moodCode);
        java.util.Date today = new Date();

        Scrip scrip = new Scrip();
        scrip.setMood(moodType);
        scrip.setSentence(sentence);
        scrip.setContent(content);
        scrip.setUserId(userId);
        scrip.setTime(today);
        scripMapper.insert(scrip);

        moodMapper.deleteByUserAndDate(userId, today);

        Mood newMood = new Mood();
        newMood.setUserId(userId);
        newMood.setDate(today);
        newMood.setMood(moodType);
        moodMapper.insert(newMood);

        return new ResponseData(200, "success", scrip);
    }
}
