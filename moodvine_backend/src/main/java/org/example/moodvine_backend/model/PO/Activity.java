package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @TableField("finish_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;

    private String picture;

    private Integer number;


    @TableField(exist = false)
    private Boolean isSignUp;

}
