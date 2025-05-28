package org.example.moodvine_backend.controller;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.mapper.DiaryMapper;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/diary")
@RestController
public class DiaryController {
    @Autowired
    DiaryService diaryService;

    @PostMapping("/getDiaries")
    public ResponseData getDiariesByMonth(@CurrentUser User user, @RequestParam String date) {
        Integer year = Integer.parseInt(date.substring(0, 4));
        Integer month = Integer.parseInt(date.substring(5, 7));
        return diaryService.getDiariesByMonth(user.getId(), year, month);
    }

    @PostMapping("/addDiary")
    public ResponseData addDiary(@CurrentUser User user,@RequestParam String content, @RequestParam String picture, @RequestParam String rewardId ) {
        return diaryService.addDiary(user.getId(),content,picture,rewardId);
    }

}
