package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("scrip")
public class Scrip {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(typeHandler = MybatisEnumTypeHandler.class)
    private MoodType mood;

    private String sentence;//一句话总结

    private String content;//内容

    private Date time;

    @TableField("user_id")
    private Integer userId; //用户id
}