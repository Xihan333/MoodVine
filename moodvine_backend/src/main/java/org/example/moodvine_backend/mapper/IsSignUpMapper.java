package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.IsSignUp;

import java.util.List;

@Mapper
public interface IsSignUpMapper extends BaseMapper<IsSignUp> {
    @Select("SELECT COUNT(*) FROM isSignUp WHERE user_id = #{userId} AND activity_id = #{activityId}")
    boolean existByUserAndActivity(
            @Param("userId") Integer userId,
            @Param("activityId") Integer activityId
    );


    @Select("SELECT activity_id FROM isSignUp WHERE user_id = #{userId}")
    List<Integer> selectActivityIdByUserId(@Param("userId") Integer userId);


    @Delete("DELETE FROM isSignUp WHERE activity_id = #{activityId}")
    int deleteByActivityId(@Param("activityId") Integer activityId);
}
