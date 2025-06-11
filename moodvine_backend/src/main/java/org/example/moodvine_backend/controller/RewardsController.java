package org.example.moodvine_backend.controller;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.annotation.CustomParam;
import org.example.moodvine_backend.model.PO.Reward;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.IsHadRewardsService;
import org.example.moodvine_backend.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/user/reward")
@RestController
public class RewardsController {
    @Autowired
    RewardService rewardService;

    @Autowired
    IsHadRewardsService isHadRewardsService;

    @GetMapping("/getAllRewards")
    public ResponseData getAllRewards(@CurrentUser User user) {
        return isHadRewardsService.getALLRewardsByUserId(user.getId());
    }

    @PostMapping("/redeemReward")
    public ResponseData redeemReward(@CurrentUser User user, @RequestBody Map<String, Integer> request) {
        Integer rewardId = request.get("id");
        return isHadRewardsService.buyReward(user.getId(), rewardId);
    }

}
