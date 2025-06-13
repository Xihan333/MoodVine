package org.example.moodvine_backend.service;

import org.example.moodvine_backend.model.DTO.ActivityDTO;
import org.example.moodvine_backend.model.DTO.EditActivityDTO;
import org.example.moodvine_backend.model.VO.ResponseData;


public interface ActivityService {
    ResponseData signUpActivity(Integer userId, Integer activityId);
    ResponseData getUserActivities(Integer userId);
    ResponseData getAllActivitiesWithSignUpStatus(Integer userId);
    ResponseData getAllActivities();
    ResponseData addActivity(ActivityDTO activityDTO);
    ResponseData editActivity(EditActivityDTO editActivityDTO);
}
