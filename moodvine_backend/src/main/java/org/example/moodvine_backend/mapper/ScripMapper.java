package org.example.moodvine_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.Scrip;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScripMapper {
    // 查询所有纸条，按时间倒序排列
    @Select("SELECT * FROM scrip WHERE user_id = #{userId} ORDER BY time DESC")
    List<Scrip> getAllScrips(Integer userId);

    // 查询最新的5张纸条，按时间倒序排列
    @Select("SELECT * FROM scrip WHERE user_id = #{userId} ORDER BY time DESC LIMIT 5")
    List<Scrip> getIndexScrips(Integer userId);

    // 查询指定id的纸条内容
    @Select("SELECT * FROM scrip WHERE id = #{id}")
    Scrip getScripDetail(Integer id);
}
