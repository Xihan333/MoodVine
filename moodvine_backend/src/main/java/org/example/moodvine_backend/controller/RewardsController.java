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

@RequestMapping("/reward")
@RestController
public class RewardsController {
    @Autowired
    RewardService rewardService;

    @Autowired
    IsHadRewardsService isHadRewardsService;

    @GetMapping("/getAllRewards")
    public ResponseData getAllRewards(@CurrentUser User user) {
        return isHadRewardsService.getALLRewardsByUserId(user);
    }

    @PostMapping("/redeemReward")
    public ResponseData redeemReward(@CurrentUser User user, @CustomParam Integer rewardId) {
        return isHadRewardsService.buyReward(user.getId(),rewardId);
    }

}
