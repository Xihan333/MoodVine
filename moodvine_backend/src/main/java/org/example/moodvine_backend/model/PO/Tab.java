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
@TableName("tab")
public class Tab {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(typeHandler = MybatisEnumTypeHandler.class)
    private Origin origin;

    private String content;//内容

    private Date date;

    @TableField("user_id")
    private Integer userId; //用户id
}