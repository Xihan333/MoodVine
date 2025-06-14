package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.TabMapper;
import org.example.moodvine_backend.model.PO.Origin;
import org.example.moodvine_backend.model.PO.Tab;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TabService {

    @Autowired
    private TabMapper tabMapper;

    // 查询指定用户的所有标签
    public ResponseData getTabsByUserId(Integer userId) {
        Date date = new Date();
        List<Tab> tabs = tabMapper.getTabsByUserAndDate(userId,date);
        List<Map<String,Object>> formattedTabs = tabs.stream().map(tab -> {
            Map<String,Object> map = new HashMap<>();
            map.put("origin",Integer.parseInt(tab.getOrigin().getCode()));
            map.put("content",tab.getContent());
            return map;
        }).collect(Collectors.toList());
        return new ResponseData(200, "查找成功", formattedTabs);
    }

    // 添加标签
    public ResponseData saveTab(Integer userId, Origin origin, String content) {
        Date date = new Date();
        int count = tabMapper.countTabsByUserAndDate(userId, date);//检查一天是否超过两条记录
        if (count >= 2) {
            return ResponseData.failure(400, "今日标签数量已达到上限");
        }
        Tab tab = new Tab();
        tab.setOrigin(origin);
        tab.setContent(content);
        tab.setUserId(userId);
        tab.setDate(date);
        tabMapper.insert(tab);
        return ResponseData.ok().msg("成功添加").data(Collections.emptyMap());
    }
}
