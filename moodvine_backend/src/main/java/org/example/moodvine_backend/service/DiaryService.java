package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.DiaryMapper;
import org.example.moodvine_backend.model.PO.Diary;
import org.example.moodvine_backend.model.VO.DiaryVO;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiaryService {
    @Autowired
    DiaryMapper diaryMapper;

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

    public ResponseData addDiary(Integer userId, String content, List<String> pictureList, Integer rewardId) {
        String pictures = String.join(",", pictureList);
        Diary diary = new Diary();
        diary.setContent(content);
        diary.setPictures(pictures);
        diary.setNotepaper(rewardId);
        diary.setUserId(userId);
        diary.setDate(new Date());
        diaryMapper.insert(diary);
        return ResponseData.ok().msg("记录成功").data(Collections.emptyMap());
    }
}
