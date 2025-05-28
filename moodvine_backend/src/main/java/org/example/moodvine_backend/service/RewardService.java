package org.example.moodvine_backend.service;

import org.example.moodvine_backend.cache.IGlobalCache;
import org.example.moodvine_backend.mapper.RewardMapper;
import org.example.moodvine_backend.mapper.UserMapper;
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
public class RewardService {

    @Autowired
    RewardMapper rewardMapper;

    public ResponseData findALLRewards(User user){
        List<Reward> allRewards = rewardMapper.findAllRewards();
        List dataList = new ArrayList();
        Map m;
        for(Reward reward : allRewards){
            m = new HashMap();
            m.put("reward_id", reward.getId());
            m.put("content", reward.getName());
            m.put("point", reward.getPrice());
            dataList.add(m);
        }
        return ResponseData.success(dataList);
    }
}
