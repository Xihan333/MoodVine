package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.Mood;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MoodMapper extends BaseMapper<Mood> {

    @Select("SELECT date, mood FROM mood WHERE user_id = #{userId} AND DATE_FORMAT(date, '%Y-%m') = #{month}")
    List<Mood> findMoodsByUserIdAndMonth(@Param("userId") Integer userId, @Param("month") String month);

    @Select("SELECT * FROM mood " + "WHERE user_id = #{userId} " + "AND date BETWEEN #{start} AND #{end}")
    List<Mood> findMoodByDateRange(@Param("userId") Integer userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
} 