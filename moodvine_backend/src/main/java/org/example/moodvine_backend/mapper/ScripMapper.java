package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.Diary;
import org.example.moodvine_backend.model.PO.Scrip;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ScripMapper extends BaseMapper<Scrip> {
    // 查询所有纸条，按时间倒序排列
    @Select("SELECT * FROM scrip WHERE user_id = #{userId} ORDER BY time DESC")
    List<Scrip> getAllScrips(Integer userId);

    // 查询最新的5张纸条，按时间倒序排列
    @Select("SELECT * FROM scrip WHERE user_id = #{userId} ORDER BY time DESC LIMIT 5")
    List<Scrip> getIndexScrips(Integer userId);

    // 查询指定id的纸条内容
    @Select("SELECT * FROM scrip WHERE id = #{id}")
    Scrip getScripDetail(Integer id);

    // 获取指定日期范围内的聊愈纸条数量
    @Select("SELECT COUNT(*) FROM scrip WHERE user_id = #{userId} AND time BETWEEN #{startDate} AND #{endDate}")
    Integer getScripsByDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

}
