package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.ClockInActivity;

public interface ClockInActivityMapper extends BaseMapper<ClockInActivity> {
    @Select("SELECT * FROM clockInActivity WHERE user_id = #{userId} AND activity_id = #{activityId} AND date = CURDATE()")
    ClockInActivity selectTodayClockIn(@Param("userId") Integer userId, @Param("activityId") Integer activityId);
}
