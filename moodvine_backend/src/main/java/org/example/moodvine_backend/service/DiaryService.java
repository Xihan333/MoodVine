package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.DiaryMapper;
import org.example.moodvine_backend.mapper.MoodMapper;
import org.example.moodvine_backend.mapper.TabMapper;
import org.example.moodvine_backend.model.PO.*;
import org.example.moodvine_backend.model.VO.DiaryVO;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiaryService {
    @Autowired
    DiaryMapper diaryMapper;

    @Autowired
    @Qualifier("emotionAnalysisClient")
    private ChatClient emotionAnalysisClient;

    @Autowired
    private MoodMapper moodMapper;

    @Autowired
    private ChatClient labelAnalysisClient;
    @Autowired
    private TabMapper tabMapper;

    public ResponseData getDiariesByMonth(Integer userId, Integer year, Integer month) {
        List<Diary> diaries = diaryMapper.getDiariesByMonth(userId, year, month);
        List<DiaryVO> diaryList = diaries.stream().map(diary -> {
            DiaryVO diaryVO = new DiaryVO();
            diaryVO.setId(diary.getId());
            diaryVO.setDate(diary.getDate());
            diaryVO.setContent(diary.getContent());
            diaryVO.setPictures(diary.getPicturesList());
            diaryVO.setNotepaper(diary.getNotepaper());
            return diaryVO;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("diaries", diaryList);
        return new ResponseData(200, "", data);
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

    public ResponseData addDiary(Integer userId, String content, List<String> pictureList, Integer rewardId) {
        String pictures = String.join(",", pictureList);
        Diary diary = new Diary();
        diary.setContent(content);
        diary.setPictures(pictures);
        diary.setNotepaper(rewardId);
        diary.setUserId(userId);
        diary.setDate(new Date());
        diaryMapper.insert(diary);

        // 使用情绪分析客户端获取情绪类型
        String moodResponse  = emotionAnalysisClient.prompt()
                .user(content)
                .call()
                .content();

        String moodCode = extractMoodCode(moodResponse);

        MoodType moodType = MoodType.fromCode(moodCode);
        Date today = new Date();

        moodMapper.deleteByUserAndDate(userId, today);

        Mood newMood = new Mood();
        newMood.setUserId(userId);
        newMood.setDate(today);
        newMood.setMood(moodType);
        moodMapper.insert(newMood);

        // 分析标签
        String labelResponse = labelAnalysisClient.prompt()
                .user(content)
                .call()
                .content();

        String label = extractSentence(labelResponse);

        System.out.println("---------------label-------------------");
        System.out.println(label);
        System.out.println("---------------label-------------------");

        tabMapper.deleteByUserAndDate(userId, today, Origin.fromCode("1"));

        Tab newTab = new Tab();
        newTab.setUserId(userId);
        newTab.setDate(today);
        newTab.setContent(label);
        newTab.setOrigin(Origin.fromCode("1"));
        tabMapper.insert(newTab);

        return ResponseData.ok().msg("记录成功").data(Collections.emptyMap());
    }
}
