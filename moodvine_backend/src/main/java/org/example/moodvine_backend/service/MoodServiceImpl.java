package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.MoodMapper;
import org.example.moodvine_backend.model.PO.Mood;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MoodServiceImpl implements MoodService {

    @Autowired
    private MoodMapper moodMapper;

    @Override
    public ResponseData getMoodsByMonth(Integer userId, String month) {
        if (userId == null || month == null || month.isEmpty()) {
            return ResponseData.failure(400, "用户ID和月份不能为空");
        }

        List<Mood> moods = moodMapper.findMoodsByUserIdAndMonth(userId, month);

        List<Map<String, Object>> formattedMoods = moods.stream().map(mood -> {
            Map<String, Object> moodMap = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            moodMap.put("date", sdf.format(mood.getDate()));
            if (mood.getMood() != null) {
                moodMap.put("mood", Integer.parseInt(mood.getMood().getCode()));
            } else {
                moodMap.put("mood", 0);
            }
            return moodMap;
        }).collect(Collectors.toList());

        return ResponseData.success(formattedMoods);
    }
}