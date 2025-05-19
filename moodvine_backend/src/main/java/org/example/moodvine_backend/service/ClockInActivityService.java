package org.example.moodvine_backend.service;

import org.example.moodvine_backend.model.VO.ResponseData;

import java.util.List;

public interface ClockInActivityService {
    ResponseData clockInActivity (Integer userId, Integer activityId, String content, List<String> pictures);

}
