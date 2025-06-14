package org.example.moodvine_backend.service;

import org.example.moodvine_backend.model.VO.ResponseData;

public interface MoodService {
    ResponseData getMoodsByMonth(Integer userId, String month);

    ResponseData getMoodCalendar(Integer userId);
} 