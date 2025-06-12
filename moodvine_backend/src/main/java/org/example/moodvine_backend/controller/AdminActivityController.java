package org.example.moodvine_backend.controller;


import org.example.moodvine_backend.model.DTO.ActivityDTO;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/activity")
public class AdminActivityController {
    private final ActivityService activityService;

    @Autowired
    public AdminActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/getAllActivities")
    public ResponseData getAllActivities() {
        return activityService.getAllActivities();
    }

    @PostMapping("/add")
    public ResponseData addActivity(@RequestBody ActivityDTO activityDTO) {
        return activityService.addActivity(activityDTO);
    }
}
