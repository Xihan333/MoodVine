package org.example.moodvine_backend.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {  //User : 有关用户的全部信息

    //用户id
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String openId;

    private String email; //邮箱

    @TableField("nick_name")
    private String nickName;  //昵称

    private Date birthday;  //生日

    private String password;  //由密码

    private String signature;  //个性签名

    private String avatar="";  //待添加默认头像图片url

    private Gender gender; //性别

    @TableField(typeHandler = MybatisEnumTypeHandler.class)
    private UserType userType;

    private int score; //蜜罐值

}
