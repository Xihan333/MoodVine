package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.Diary;

import java.util.List;

@Mapper
public interface DiaryMapper extends BaseMapper<Diary> {
    //获取指定月份的日记
    @Select("SELECT * FROM diary where user_id=#{user_id} and YEAR(date)=#{year} and YEAR(date)=#{month}")
    List<Diary> getDiariesByMonth(Integer user_id, Integer year,Integer month);

    //添加日记
    @Insert("insert into diary(content, picture,  user_id, reward_id) values (#{content}, #{picture}, #{userId}, #{rewardId})")
    void addDiary(Integer userId, String picture, String content, String rewardId);

}
