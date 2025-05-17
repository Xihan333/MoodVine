package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.moodvine_backend.model.PO.Reward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RewardMapper extends BaseMapper<Reward> {
    // 查询所有信纸
    @Select("SELECT * FROM reward")
    List<Reward> findAllRewards();

    //查找对应信纸
    @Select("SELECT * FROM reward where id=#{reward_id}")
    Reward findRewardById(Integer reward_id);

}
