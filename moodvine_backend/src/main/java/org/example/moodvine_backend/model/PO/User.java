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
@TableName("user")
public class User {  //User : 有关用户的全部信息

    //用户id
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String email; //邮箱

    private String nickName;  //昵称

    private String password;  //由密码

    private String signature;  //个性签名

    private String avatar="";  //待添加默认头像图片url

    private Gender gender; //性别

    private UserType userType;

}
