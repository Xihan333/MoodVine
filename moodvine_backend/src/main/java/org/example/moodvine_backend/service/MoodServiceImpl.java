package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.MoodMapper;
import org.example.moodvine_backend.model.PO.Mood;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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

    @Override
    public ResponseData getMoodCalendar(Integer userId) {
        if (userId == null) {
            return ResponseData.failure(400, "用户id不能为空");
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(4).withDayOfMonth(1);
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());

        List<Mood> moods = moodMapper.findMoodByDateRange(userId, startDate, endDate);

        Map<LocalDate,Mood> moodMap = moods.stream().collect(Collectors.toMap(
                mood -> mood.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                mood -> mood
        ));

        List<Map<String,Object>> calendarData = new ArrayList<>();
        for(LocalDate date = startDate;!date.isAfter(endDate);date=date.plusDays(1)) {
            Map<String,Object> dayData = new HashMap<>();

            long timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            dayData.put("date", timestamp);

            if(moodMap.containsKey(date)) {
                Mood mood = moodMap.get(date);
                dayData.put("level", Integer.parseInt(mood.getMood().getCode()));
                dayData.put("count", 0);
            }else{
                dayData.put("level", 0);
                dayData.put("count", 0);
            }
            calendarData.add(dayData);
        }
        return ResponseData.success(calendarData);
    }
}