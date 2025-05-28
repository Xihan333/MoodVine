package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("isSignUp")
public class IsSignUp {
    @TableId(type= IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;

    @TableField("activity_id")
    private Integer activityId;
}
