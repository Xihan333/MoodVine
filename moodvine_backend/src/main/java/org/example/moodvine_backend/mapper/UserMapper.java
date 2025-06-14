package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.moodvine_backend.model.PO.Reward;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.PO.UserType;
import org.example.moodvine_backend.model.PO.Gender;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT EXISTS(SELECT 1 FROM user WHERE email=#{email})") //寻找指定email的用户
    boolean judgeExistsByEmail(String email);

    @Select("select * from user where open_id=#{openId}")
    User findByOpenId(String openId);

    @Select("select * from user where email=#{email}")  //获取指定邮箱的用户
    User findByEmail(String email);

    @Select("select user_type from user where email=#{email}")  //获取指定uid的用户权限
    UserType findUserType(String email);

    @Update("update user set nick_name=#{newNickname} where id=#{userId}")  //更新user中指定uid的用户的昵称
    void updateNickname(String userId,String newNickname);

    //@Update("update user set signature=#{newSignature} where id=#{userId}")  //更新user中指定uid的用户的签名
    //void updateSignature(String userId,String newSignature);

    @Update("update user set avatar=#{avatar} where id=#{userId}")  //更新user中指定uid的用户的头像
    void updateAvatar(String userId, String avatar);

    //@Update("truncate table feedback")  //清除feedback数据库内容
    //void clear();

    @Update("update user set nick_name=#{nickName}, avatar=#{avatar} where id=#{id}")
    void update(User user);

    @Select("SELECT * FROM user where id=#{user_id}")
    User findUserById(Integer user_id);

    @Update("update user set score = score + #{addScore} where id=#{userId}")
    void addScore(@Param("userId") Integer userId, @Param("addScore") Integer addScore);

    @Update("update user set score = score - #{point} where id=#{userId}")
    void consumeScore(@Param("userId") Integer userId, @Param("point") Integer point);
}
