package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("mood")
public class Mood {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Date date;

    @TableField("user_id")
    private Integer userId;

    @TableField(typeHandler = MybatisEnumTypeHandler.class)
    private MoodType mood;

    //时间戳
    public long getDateTimestamp() {
        if (date == null) return 0;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

} 