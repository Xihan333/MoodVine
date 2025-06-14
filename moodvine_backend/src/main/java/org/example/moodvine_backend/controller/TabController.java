package org.example.moodvine_backend.controller;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.annotation.CustomParam;
import org.example.moodvine_backend.model.PO.Origin;
import org.example.moodvine_backend.model.PO.Reward;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.IsHadRewardsService;
import org.example.moodvine_backend.service.RewardService;
import org.example.moodvine_backend.service.TabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/user/tab")
@RestController
public class TabController {
    @Autowired
    TabService tabService;

    @GetMapping("/getTabs")
    public ResponseData getTabs(@CurrentUser User user) {
        return tabService.getTabsByUserId(user.getId());
    }

    @PostMapping("/saveTab")
    public ResponseData saveTab(@CurrentUser User user, @RequestBody Map<String, Object> request) {
        String code = (String) request.get("origin");
        String content = (String) request.get("content");
        Origin origin = Origin.fromCode(code);
        return tabService.saveTab(user.getId(), origin, content);
    }

}
