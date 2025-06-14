package org.example.moodvine_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.moodvine_backend.model.PO.Origin;
import org.example.moodvine_backend.model.PO.Tab;

import java.util.Date;
import java.util.List;
import java.time.LocalDate;

@Mapper
public interface TabMapper extends BaseMapper<Tab> {
    // 查询指定用户某天的标签数量
    @Select("SELECT COUNT(*) FROM tab WHERE user_id = #{userId} AND DATE(date) = DATE(#{date})")
    int countTabsByUserAndDate(Integer userId, Date date);

    // 查询指定用户某天的标签
    @Select("SELECT * FROM tab WHERE user_id = #{userId} AND DATE(date) = DATE(#{date})")
    List<Tab> getTabsByUserAndDate(Integer userId, Date date);

    @Select("SELECT * FROM tab WHERE user_id = #{userId} AND origin = #{origin} AND DATE_FORMAT(date, '%Y-%m-%d') = DATE_FORMAT(#{date}, '%Y-%m-%d'))")
    List<Tab> getTabsByUserOriginAndDate(@Param("userId") Integer userId,
                                         @Param("origin") Origin origin,
                                         @Param("date") Date date);

    // 删除用户某天的某类标签
    @Delete("DELETE FROM tab WHERE user_id = #{userId} AND origin = #{origin} AND DATE_FORMAT(date, '%Y-%m-%d') = DATE_FORMAT(#{date}, '%Y-%m-%d')")
    int deleteByUserAndDate(
            @Param("userId") Integer userId,
            @Param("date") Date date,
            @Param(("origin")) Origin origin
    );

}
