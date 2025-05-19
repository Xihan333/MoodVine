package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.ActivityMapper;
import org.example.moodvine_backend.mapper.ClockInActivityMapper;
import org.example.moodvine_backend.model.PO.Activity;
import org.example.moodvine_backend.model.PO.ClockInActivity;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ClockInActivityServiceImpl implements ClockInActivityService{
    @Autowired
    private ClockInActivityMapper clockInActivityMapper;
    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional
    public ResponseData<?> clockInActivity(Integer userId, Integer activityId, String content, List<String> pictures){
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return ResponseData.failure(400, "活动不存在或已结束");
        }

        ClockInActivity existing = clockInActivityMapper.selectTodayClockIn(userId, activityId);
        if (existing != null) {
            return ResponseData.failure(400, "今日已打卡");
        }

        ClockInActivity record = new ClockInActivity();
        record.setUserId(userId);
        record.setActivityId(activityId);
        record.setContent(content);
        record.setPictures(String.join(",", pictures));
        record.setDate(new Date());
        clockInActivityMapper.insert(record);

        return ResponseData.ok().msg("打卡成功").data(Collections.emptyMap());

    }
}
