package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("reward")
public class Reward {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name; // 信纸名称

    private String content; // 日记图片URL

    private Integer point; //所需蜜罐值

    @TableField(exist = false)
    private Boolean isHad;
}
