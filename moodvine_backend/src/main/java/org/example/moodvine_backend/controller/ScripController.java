package org.example.moodvine_backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.annotation.CustomParam;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.ChatService;
import org.example.moodvine_backend.service.ScripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/user/scrip")
@RestController
public class ScripController {
    @Autowired
    private ScripService scripService;


    @GetMapping("/getAllScrips")
    public ResponseData getAllScrips(@CurrentUser User user) {
        return scripService.getAllScrips(user.getId());
    }

    @GetMapping("/getIndexScrip")
    public ResponseData getIndexScrip(@CurrentUser User user) {
        return scripService.getIndexScrips(user.getId());
    }

    @PostMapping("/getScripDetail")
    public ResponseData getScripDetail(@CustomParam Integer id) {
        return scripService.getScripDetail(id);
    }

    @PostMapping("/saveScrip")
    public ResponseData saveScrip(@CurrentUser User user, @CustomParam String content) {
        return scripService.saveScrip(user.getId(), content);
    }

}
