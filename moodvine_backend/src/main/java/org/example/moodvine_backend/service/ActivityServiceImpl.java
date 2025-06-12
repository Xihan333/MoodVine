package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.ActivityMapper;
import org.example.moodvine_backend.mapper.IsSignUpMapper;
import org.example.moodvine_backend.model.DTO.ActivityDTO;
import org.example.moodvine_backend.model.PO.Activity;
import org.example.moodvine_backend.model.PO.IsSignUp;
import org.example.moodvine_backend.model.VO.ActivityVO;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public ResponseData getAllActivitiesWithSignUpStatus(Integer userId) {
        // 获取所有活动
        List<Activity> allActivities = activityMapper.findAllActivities();

        // 获取用户已报名的活动ID集合
        Set<Integer> signedUpActivityIds = isSignUpMapper.selectActivityIdByUserId(userId)
                                                            .stream().collect(Collectors.toSet());

        // 遍历所有活动，设置 isSignUp 标志
        allActivities.forEach(activity -> {
            activity.setIsSignUp(signedUpActivityIds.contains(activity.getId()));
        });

        // 构造响应
        Map<String, Object> data = new HashMap<>();
        data.put("activities", allActivities);

        return ResponseData.ok()
                .data(data)
                .msg("查询成功");
    }

    @Override
    public ResponseData getAllActivities(){
        List<Activity> activities = activityMapper.findAllActivities();

        List<ActivityVO> activityVOs = activities.stream().map(activity ->  {
            ActivityVO activityVO = new ActivityVO();
            activityVO.setId(activity.getId());
            activityVO.setName(activity.getName());
            activityVO.setDescription(activity.getDescription());
            activityVO.setStartTime(activity.getStartTime());
            activityVO.setFinishTime(activity.getFinishTime());
            activityVO.setPicture(activity.getPicture());
            activityVO.setNumber(activity.getNumber());
            return activityVO;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("activities", activityVOs);

        return ResponseData.ok().data(data).msg("活动列表获取成功");
    }

    @Override
    public ResponseData addActivity(ActivityDTO activityDTO) {
        if (activityDTO.getStartTime().after(activityDTO.getFinishTime())) {
            return ResponseData.failure(400, "结束时间不能早于开始时间");
        }
        Activity activity = new Activity();
        activity.setName(activityDTO.getName());
        activity.setDescription(activityDTO.getDescription());
        activity.setPicture(activityDTO.getPicture());
        activity.setStartTime(activityDTO.getStartTime());
        activity.setFinishTime(activityDTO.getFinishTime());
        activity.setNumber(0);

        int result = activityMapper.insert(activity);
        if (result > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("activity", activity.getId());
            return ResponseData.ok().msg("添加成功").data(data);
        }else  {
            return ResponseData.failure(500, "添加失败");
        }
    }
}
