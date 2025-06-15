package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.Diary;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Mapper
public interface DiaryMapper extends BaseMapper<Diary> {
    //获取指定月份的日记
    @Select("SELECT id, content, pictures, date, user_id, reward_id AS notepaper FROM diary where user_id=#{user_id} and YEAR(date)=#{year} and MONTH(date)=#{month} ORDER BY id ASC")
    List<Diary> getDiariesByMonth(Integer user_id, Integer year,Integer month);

    //添加日记
    @Insert("insert into diary(content, pictures,  user_id, reward_id,date) values (#{content}, #{picture}, #{userId}, #{rewardId},#{date})")
    void addDiary( String content, String picture, Integer rewardId,Integer userId, Date date);

    // 获取指定日期范围内的日记数量
    @Select("SELECT COUNT(*) FROM diary WHERE user_id = #{userId} AND date BETWEEN #{startDate} AND #{endDate}")
    Integer getDiariesByDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

}
