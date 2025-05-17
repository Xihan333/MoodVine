package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.moodvine_backend.model.PO.IsHadRewards;

import java.util.List;

@Mapper
public interface IsHadRewardsMapper extends BaseMapper<IsHadRewards> {

    // 根据用户ID查询所有的信纸
    @Select("SELECT * FROM isHadRewards WHERE user_id = #{userId}")
    List<IsHadRewards> getRewardByUserId(Integer userId);

    // 用户购买信纸
    @Update("update isHadRewards set isHad = true where user_id=#{userId} and reward_id=#{rewardId}")
    void buyReward(Integer userId, Integer rewardId);
}
