package org.example.moodvine_backend.controller;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.mapper.DiaryMapper;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping("/user/diary")
@RestController
public class DiaryController {
    @Autowired
    DiaryService diaryService;

    @PostMapping("/getDiaries")
    public ResponseData getDiariesByMonth(@CurrentUser User user, @RequestBody Map<String, Object> request) {
        String date = (String) request.get("date");
        //Integer userId = (Integer) request.get("userId");
        Integer year = Integer.parseInt(date.substring(0, 4));
        Integer month = Integer.parseInt(date.substring(5, 7));
        return diaryService.getDiariesByMonth(user.getId(), year, month);
    }

    @PostMapping("/addDiary")
    public ResponseData addDiary(@CurrentUser User user,@RequestBody Map<String, Object> request) {
        //Integer userId = (Integer) request.get("userId");
        String content = (String) request.get("content");
        List<String> pictures = (List<String>) request.get("pictures");
        Integer notepaper = (Integer) request.get("notepaper");
        return diaryService.addDiary(user.getId(),content,pictures,notepaper);
    }

}
