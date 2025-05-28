package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("isHadReward")
public class IsHadRewards {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer user_id;

    private Integer reward_id;

    private Boolean isHadReward;
}
