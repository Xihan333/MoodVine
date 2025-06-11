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
@TableName("diary")
public class Diary {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String content; // 日记内容

    private String pictures; // 日记图片URL

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date; // 日记日期

    @TableField("user_id")
    private Integer userId; //用户id

    @TableField("reward_id")
    private Integer notepaper; //信纸id

    public List<String> getPicturesList() {
        return (pictures != null && !pictures.isEmpty()) ?
                Arrays.asList(pictures.split(",")) :
                Collections.emptyList();
    }

    public void setPicturesList(List<String> picturesList) {
        this.pictures = String.join(",", picturesList);
    }

}
