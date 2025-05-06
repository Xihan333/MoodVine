package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.DiaryMapper;
import org.example.moodvine_backend.model.PO.Diary;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {
    @Autowired
    DiaryMapper diaryMapper;

    public ResponseData getDiariesByMonth(Integer userId, Integer year, Integer month) {
        List<Diary> diaries = diaryMapper.getDiariesByMonth(userId, year, month);
        return new ResponseData(200,"",diaries);
    }

    public ResponseData addDiary(Integer userId, String picture, String content, String rewardId) {
        diaryMapper.addDiary(userId,picture,content,rewardId);
        return new ResponseData(83,"记录成功",null);
    }
}
