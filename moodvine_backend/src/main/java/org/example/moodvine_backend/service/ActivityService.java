package org.example.moodvine_backend.service;

import org.example.moodvine_backend.model.VO.ResponseData;


public interface ActivityService {
    ResponseData signUpActivity(Integer userId, Integer activityId);
    ResponseData getUserActivities(Integer userId);
    ResponseData getAllActivitiesWithSignUpStatus(Integer userId);
    ResponseData getAllActivities();
}
