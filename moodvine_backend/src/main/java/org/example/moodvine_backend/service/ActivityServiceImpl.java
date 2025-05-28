package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.ActivityMapper;
import org.example.moodvine_backend.mapper.IsSignUpMapper;
import org.example.moodvine_backend.model.PO.Activity;
import org.example.moodvine_backend.model.PO.IsSignUp;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService{
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private IsSignUpMapper isSignUpMapper;


    @Override
    @Transactional
    public ResponseData<?> signUpActivity(Integer userId,Integer activityId){
        if (activityId == null) {
            return ResponseData.failure(400, "活动ID不能为空").data(Collections.emptyMap());
        }

        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return ResponseData.notFound().msg("活动不存在");
        }

        boolean isSigned = isSignUpMapper.existByUserAndActivity(userId, activityId);
        if(isSigned){
            return ResponseData.failure(400,"请勿重复报名").data(Collections.emptyMap());
        }

        IsSignUp signUp = new IsSignUp();
        signUp.setUserId(userId);
        signUp.setActivityId(activityId);
        isSignUpMapper.insert(signUp);

        activityMapper.incrementParticipantNumber(activityId);

        return ResponseData.ok().msg("报名成功").data(Collections.emptyMap());
    }

    @Override
    public ResponseData getUserActivities(Integer userId) {
        // 获取用户报名的活动ID列表
        List<Integer> activityIds = isSignUpMapper.selectActivityIdByUserId(userId);
        if (activityIds.isEmpty()) {
            return ResponseData.ok()
                    .data(Collections.singletonMap("activities", Collections.emptyList()))
                    .msg("用户暂未报名任何活动");
        }

        // 批量查询活动详情
        List<Activity> activities = activityMapper.selectBatchIds(activityIds);

        // 设置报名标志
        activities.forEach(act -> act.setIsSignUp(true));

        // 构造响应
        Map<String, Object> data = new HashMap<>();
        data.put("activities", activities);

        return ResponseData.ok()
                .data(data)
                .msg("查询成功");
    }


}
