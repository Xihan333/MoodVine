package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.ActivityMapper;
import org.example.moodvine_backend.mapper.ClockInActivityMapper;
import org.example.moodvine_backend.model.PO.Activity;
import org.example.moodvine_backend.model.PO.ClockInActivity;
import org.example.moodvine_backend.model.VO.ClockInVo;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
            return ResponseData.failure(400, "活动不存在");
        }


        if (content == null || content.trim().isEmpty()) {
            return ResponseData.failure(400, "打卡内容不能为空");
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


    @Override
    public ResponseData<?> getClockIns(Integer userId, Integer activityId){
        Activity activity = activityMapper.selectActivityById(activityId);
        if (activity == null) {
            return ResponseData.notFound().msg("活动不存在");
        }

        //计算活动天数
        long period = ChronoUnit.DAYS.between(
                activity.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                activity.getFinishTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        ) + 1;


        List<ClockInActivity> records = clockInActivityMapper.selectClockInsByUserAndActivity(userId, activityId);

        List<ClockInVo> clockIns = records.stream().map(record -> {
            ClockInVo vo = new ClockInVo();
            vo.setId(record.getId());
            vo.setDate(record.getDate());
            vo.setContent(record.getContent());
            vo.setPictures(record.getPicturesList());
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("period", period);
        data.put("clockIns", clockIns);

        return ResponseData.ok().data(data).msg("查询成功");
    }
}
