package org.example.moodvine_backend.controller;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/user/activity")
public class UserActivityController {
    @Autowired
    private ActivityService activityService;

    @GetMapping("/getAllActivities")
    public ResponseData getAllActivities(@CurrentUser User user){
        return activityService.getUserActivities(user.getId());
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
        Integer userId = request.get("userId");
        Integer activityId = request.get("activityId");
        return activityService.signUpActivity(user.getId(),activityId);
//        return activityService.signUpActivity(userId,activityId);
    }
}
