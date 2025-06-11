package org.example.moodvine_backend.service;

import org.example.moodvine_backend.cache.IGlobalCache;
import org.example.moodvine_backend.mapper.IsHadRewardsMapper;
import org.example.moodvine_backend.mapper.RewardMapper;
import org.example.moodvine_backend.mapper.UserMapper;
import org.example.moodvine_backend.model.PO.IsHadRewards;
import org.example.moodvine_backend.model.PO.Reward;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IsHadRewardsService {

    @Autowired
    RewardMapper rewardMapper;

    @Autowired
    IsHadRewardsMapper isHadRewardsMapper;

    @Autowired
    UserMapper userMapper;

    public ResponseData getALLRewardsByUserId(Integer userId) {
        List<Reward> allRewards = rewardMapper.findAllRewards();
        Set<Integer> ownedRewardIds = isHadRewardsMapper.getRewardByUserId(userId).stream().collect(Collectors.toSet());

        allRewards.forEach(reward -> {
            reward.setIsHad(ownedRewardIds.contains(reward.getId()));
        });


        Map<String, Object> responseData = new HashMap<>();
        responseData.put("rewards", allRewards);

        return ResponseData.success(responseData);
    }

    public ResponseData buyReward(Integer userId,Integer reward_id){
        Reward reward = rewardMapper.findRewardById(reward_id);
        User user = userMapper.findUserById(userId);
        if(user.getScore()<reward.getPoint()){
            return ResponseData.failure(400,"蜜罐值不足").data(Collections.emptyMap());
        }
        userMapper.consumeScore(userId,reward.getPoint());
        isHadRewardsMapper.buyReward(userId,reward_id);
        return ResponseData.ok().msg("成功兑换").data(Collections.emptyMap());
    }
}
