package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("diary")
public class Diary {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String content; // 日记内容

    private String picture; // 日记图片URL

    private Date date; // 日记日期

    private Integer userId; //用户id

    private Integer rewardId; //信纸id

}
