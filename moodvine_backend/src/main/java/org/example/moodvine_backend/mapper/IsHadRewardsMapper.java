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
    @Select("SELECT reward_id FROM isHadRewards WHERE user_id = #{userId}")
    List<Integer> getRewardByUserId(Integer userId);

    // 用户购买信纸
    @Update("INSERT INTO isHadRewards (user_id, reward_id) VALUES (#{userId}, #{rewardId}) ON DUPLICATE KEY UPDATE user_id = user_id")
    void buyReward(Integer userId, Integer rewardId);

    // 根据用户ID和奖励ID查询用户是否拥有该奖励
    @Select("SELECT EXISTS(SELECT 1 FROM isHadRewards WHERE user_id = #{userId} AND reward_id = #{rewardId} LIMIT 1)")
    Boolean isUserHasReward(Integer userId, Integer rewardId);
}
