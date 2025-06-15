package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.moodvine_backend.model.PO.Mood;
import org.example.moodvine_backend.model.PO.MoodType;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Mapper
public interface MoodMapper extends BaseMapper<Mood> {

    @Select("SELECT date, mood FROM mood WHERE user_id = #{userId} AND DATE_FORMAT(date, '%Y-%m') = #{month} ORDER BY date ASC" )
    List<Mood> findMoodsByUserIdAndMonth(@Param("userId") Integer userId, @Param("month") String month);

    @Select("SELECT * FROM mood " + "WHERE user_id = #{userId} " + "AND date BETWEEN #{start} AND #{end}")
    List<Mood> findMoodByDateRange(@Param("userId") Integer userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT COUNT(*) > 0 FROM mood WHERE user_id = #{userId} AND DATE(date) = CURRENT_DATE()")
    boolean hasMoodToday(@Param("userId") Integer userId);

    @Update("UPDATE mood SET mood = #{mood}" +
            "WHERE user_id = #{userId} AND DATE(date) = CURRENT_DATE()")
    int updateTodayMood(
            @Param("userId") Integer userId,
            @Param("mood") MoodType mood
    );

    // 删除用户某天的所有记录
    @Delete("DELETE FROM mood WHERE user_id = #{userId} AND DATE_FORMAT(date, '%Y-%m-%d') = DATE_FORMAT(#{date}, '%Y-%m-%d')")
    int deleteByUserAndDate(
            @Param("userId") Integer userId,
            @Param("date") Date date
    );
} 