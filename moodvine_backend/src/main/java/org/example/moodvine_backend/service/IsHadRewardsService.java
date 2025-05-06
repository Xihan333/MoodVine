package org.example.moodvine_backend.service;

import org.example.moodvine_backend.cache.IGlobalCache;
import org.example.moodvine_backend.mapper.IsHadRewardsMapper;
import org.example.moodvine_backend.mapper.RewardMapper;
import org.example.moodvine_backend.model.PO.IsHadRewards;
import org.example.moodvine_backend.model.PO.Reward;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IsHadRewardsService {

    @Autowired
    RewardMapper rewardMapper;

    @Autowired
    IsHadRewardsMapper isHadRewardsMapper;

    public ResponseData getALLRewardsByUserId(User user){
        List<IsHadRewards> allRewards = isHadRewardsMapper.getRewardByUserId(user.getId());
        List dataList = new ArrayList();
        Map m;
        for(IsHadRewards isHadRewards : allRewards){
            m = new HashMap();
            Reward reward = rewardMapper.findRewardById(isHadRewards.getReward_id());
            m.put("id", isHadRewards.getReward_id());
            m.put("name", reward.getName());
            m.put("content", reward.getImageUrl());
            m.put("point", reward.getPrice());
            m.put("isHad", isHadRewards.getIsHadReward());
            dataList.add(m);
        }
        return ResponseData.success(dataList);
    }

    public ResponseData buyReward(Integer user_id,Integer reward_id){
        isHadRewardsMapper.buyReward(user_id,reward_id);
        return new ResponseData(200,"成功兑换",null);
    }
}
