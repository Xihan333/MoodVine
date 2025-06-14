package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.ClockInActivity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ClockInActivityMapper extends BaseMapper<ClockInActivity> {
    @Select("SELECT * FROM clockInActivity WHERE user_id = #{userId} AND activity_id = #{activityId} AND date = CURDATE()")
    ClockInActivity selectTodayClockIn(@Param("userId") Integer userId, @Param("activityId") Integer activityId);

    @Select("SELECT id, content, picture AS pictures, date, activity_id, user_id " +
            "FROM clockInActivity " +
            "WHERE user_id = #{userId} AND activity_id = #{activityId} " +
            "ORDER BY date ASC")
    List<ClockInActivity> selectClockInsByUserAndActivity(
            @Param("userId") Integer userId,
            @Param("activityId") Integer activityId
    );


    @Select("SELECT COUNT(*) FROM clockInActivity WHERE activity_id = #{activityId}")
    int countClockInRecordsByActivityId(@Param("activityId") Integer activityId);

    // 获取指定日期范围内的打卡数量
    @Select("SELECT COUNT(*) FROM clockInActivity WHERE user_id = #{userId} AND date BETWEEN #{startDate} AND #{endDate}")
    Integer getClockInByDateRange(Integer userId, LocalDate startDate, LocalDate endDate);
}
