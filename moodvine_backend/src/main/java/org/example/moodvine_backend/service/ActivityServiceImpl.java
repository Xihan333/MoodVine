package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.ActivityMapper;
import org.example.moodvine_backend.mapper.ClockInActivityMapper;
import org.example.moodvine_backend.mapper.IsSignUpMapper;
import org.example.moodvine_backend.model.DTO.ActivityDTO;
import org.example.moodvine_backend.model.DTO.EditActivityDTO;
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

    @Autowired
    private ClockInActivityMapper clockInActivityMapper;

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
        List<Integer> activityIds = isSignUpMapper.selectActivityIdByUserId(userId);
        if (activityIds.isEmpty()) {
            return ResponseData.ok()
                    .data(Collections.singletonMap("activities", Collections.emptyList()))
                    .msg("用户暂未报名任何活动");
        }

        // 批量查询活动
        List<Activity> activities = activityMapper.selectBatchIds(activityIds);

        activities.forEach(act -> act.setIsSignUp(true));

        Map<String, Object> data = new HashMap<>();
        data.put("activities", activities);

        return ResponseData.ok()
                .data(data)
                .msg("查询成功");
    }

    @Override
    public ResponseData getAllActivitiesWithSignUpStatus(Integer userId) {
        List<Activity> allActivities = activityMapper.findAllActivities();

        // 获取用户已报名的活动ID集合
        Set<Integer> signedUpActivityIds = isSignUpMapper.selectActivityIdByUserId(userId)
                                                            .stream().collect(Collectors.toSet());

        // 遍历所有活动，设置 isSignUp 标志
        allActivities.forEach(activity -> {
            activity.setIsSignUp(signedUpActivityIds.contains(activity.getId()));
        });

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
        if (activityDTO.getName() == null || activityDTO.getName().trim().isEmpty()) {
            return ResponseData.failure(400, "活动名称不能为空");
        }
        if (activityDTO.getDescription() == null || activityDTO.getDescription().trim().isEmpty()) {
            return ResponseData.failure(400, "活动描述不能为空");
        }
        if (activityDTO.getPicture() == null || activityDTO.getPicture().trim().isEmpty()) {
            return ResponseData.failure(400, "活动图片不能为空");
        }
        if (activityDTO.getStartTime() == null) {
            return ResponseData.failure(400, "开始时间不能为空");
        }
        if (activityDTO.getFinishTime() == null) {
            return ResponseData.failure(400, "结束时间不能为空");
        }
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

    @Override
    @Transactional
    public ResponseData editActivity(EditActivityDTO editActivityDTO) {
        if (editActivityDTO.getId() == null) {
            return ResponseData.failure(400, "活动ID不能为空");
        }

        Activity activity = activityMapper.selectById(editActivityDTO.getId());
        if (activity == null) {
            return ResponseData.notFound().msg("活动不存在");
        }

        if (editActivityDTO.getName() != null) {
            if (editActivityDTO.getName().trim().isEmpty()) {
                return ResponseData.failure(400, "活动名称不能为空");
            }
            activity.setName(editActivityDTO.getName());
        }

        if (editActivityDTO.getDescription() != null) {
            if (editActivityDTO.getDescription().trim().isEmpty()) {
                return ResponseData.failure(400, "活动描述不能为空");
            }
            activity.setDescription(editActivityDTO.getDescription());
        }

        if (editActivityDTO.getPicture() != null) {
            if (editActivityDTO.getPicture().trim().isEmpty()) {
                return ResponseData.failure(400, "活动图片不能为空");
            }
            activity.setPicture(editActivityDTO.getPicture());
        }

        if (editActivityDTO.getStartTime() != null &&
                editActivityDTO.getFinishTime() != null) {

            if (editActivityDTO.getStartTime().after(editActivityDTO.getFinishTime())) {
                return ResponseData.failure(400, "结束时间不能早于开始时间");
            }
        }

        if (editActivityDTO.getStartTime() != null) {
            activity.setStartTime(editActivityDTO.getStartTime());
        }

        if (editActivityDTO.getFinishTime() != null) {
            activity.setFinishTime(editActivityDTO.getFinishTime());
        }
        int result = activityMapper.updateById(activity);
        if (result > 0) {
            return ResponseData.ok().msg("修改成功");
        } else {
            return ResponseData.failure(500, "修改失败");
        }
    }


    @Override
    @Transactional
    public ResponseData deleteActivity(Integer activityId) {
        if (activityId == null) {
            return ResponseData.failure(400, "活动ID不能为空");
        }

        Activity activity = activityMapper.selectActivityById(activityId);
        if (activity == null) {
            return ResponseData.notFound().msg("活动不存在");
        }

        int clockInCount = clockInActivityMapper.countClockInRecordsByActivityId(activityId);
        if (clockInCount > 0) {
            return ResponseData.failure(400, "该活动已有打卡记录，无法删除活动");
        }

        int deleteSignUpCount = isSignUpMapper.deleteByActivityId(activityId);

        int deleteActivityResult = activityMapper.deleteActivityById(activityId);

        if (deleteActivityResult > 0) {
            return ResponseData.ok().msg("删除成功");
        } else {
            return ResponseData.failure(500, "删除活动失败");
        }
    }
}
