package org.example.moodvine_backend.controller;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.ActivityService;
import org.example.moodvine_backend.service.ClockInActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user/activity")
public class UserActivityController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ClockInActivityService clockInActivityService;

    @GetMapping("/getAllActivities")
    public ResponseData getAllActivities(@CurrentUser User user){
        return activityService.getAllActivitiesWithSignUpStatus(user.getId());
    }

//    @GetMapping("/getAllActivities")
//    public ResponseData getAllActivities(@RequestParam Integer userId) {
//        return activityService.getUserActivities(userId);
//    }

    @PostMapping("/signUp")
    public ResponseData signUp(
            @CurrentUser User user,
            @RequestBody Map<String,Integer> request
    ){
//        Integer userId = request.get("userId");
        Integer activityId = request.get("activityId");
//        return activityService.signUpActivity(userId,activityId);
       return activityService.signUpActivity(user.getId(),activityId);
    }

    @PostMapping("/clockIn")
    public ResponseData clockIn(
            @CurrentUser User user,
            @RequestBody Map<String, Object> request
    ){
//        Integer userId = (Integer) request.get("userId");
        Integer activityId = (Integer) request.get("activityId");
        String content = (String) request.get("content");
        List<String> pictures = (List<String>) request.get("pictures");

        if (activityId == null || content == null) {
            return ResponseData.failure(400, "参数缺失");
        }

        return clockInActivityService.clockInActivity(user.getId(), activityId, content, pictures);
//        return clockInActivityService.clockInActivity(userId, activityId, content, pictures);

    }

    @PostMapping("/getClockIns")
    public ResponseData getClockIns(
            @CurrentUser User user,
            @RequestBody Map<String, Integer> request
    ){
//        Integer userId = (Integer) request.get("userId");
        Integer activityId = request.get("activityId");
        if (activityId == null) {
            return ResponseData.failure(400, "活动ID不能为空");
        }
        return clockInActivityService.getClockIns(user.getId(), activityId);
//        return clockInActivityService.getClockIns(userId, activityId);
    }
}
