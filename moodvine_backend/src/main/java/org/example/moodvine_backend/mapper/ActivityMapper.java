package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.moodvine_backend.model.PO.Activity;
import org.example.moodvine_backend.model.PO.Reward;

import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    @Select("<script>" +
            "SELECT * FROM activity WHERE id IN " +
            "<foreach item='id' collection='activityIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Activity> selectBatchIds(@Param("activityIds") List<Integer> activityIds);

    @Select("SELECT * FROM activity")
    List<Activity> findAllActivities();

    @Update("UPDATE activity SET number = number + 1 WHERE id = #{activityId}")
    int incrementParticipantNumber(@Param("activityId") Integer activityId);

    @Select("SELECT * FROM activity WHERE id = #{activityId}")
    Activity selectActivityById(@Param("activityId") Integer activityId);

    @Insert("INSERT INTO activity(name,description,start_time,finish_time,picture,number) " + "VALUES (#{name}, #{description}, #{start_time}, #{finish_time}, #{picture}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertActivity(Activity activity);
}
