package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("clockInActivity")
public class ClockInActivity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String content;

    @TableField("picture")
    private String pictures; // 存储为JSON字符串

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @TableField("activity_id")
    private Integer activityId;

    @TableField("user_id")
    private Integer userId;

    public List<String> getPicturesList() {
        return (pictures != null && !pictures.isEmpty()) ?
                Arrays.asList(pictures.split(",")) :
                Collections.emptyList();
    }
}
