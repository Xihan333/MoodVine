package org.example.moodvine_backend.service;

import org.example.moodvine_backend.mapper.ScripMapper;
import org.example.moodvine_backend.model.PO.Scrip;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScripService {
    @Autowired
    private ScripMapper scripMapper;

    public ResponseData getAllScrips(Integer userId) {
        List<Scrip> scrips = scripMapper.getAllScrips(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        List<Map<String,Object>> formattedScrips = scrips.stream().map(scrip -> {
            Map<String,Object> map = new HashMap<>();
            map.put("id",scrip.getId());
            map.put("mood",Integer.parseInt(scrip.getMood().getCode()));
            map.put("sentence",scrip.getSentence());
            map.put("time",sdf.format(scrip.getTime()));
            return map;
        }).collect(Collectors.toList());
        return new ResponseData(200, "成功", formattedScrips);
    }

    public ResponseData getIndexScrips(Integer userId) {
        List<Scrip> scrips = scripMapper.getIndexScrips(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        List<Map<String,Object>> formattedScrips = scrips.stream().map(scrip -> {
            Map<String,Object> map = new HashMap<>();
            map.put("id",scrip.getId());
            map.put("mood",Integer.parseInt(scrip.getMood().getCode()));
            map.put("sentence",scrip.getSentence());
            map.put("time",sdf.format(scrip.getTime()));
            return map;
        }).collect(Collectors.toList());
        return new ResponseData(200, "成功", formattedScrips);
    }

    public ResponseData getScripDetail(Integer id) {
        Scrip scripDetail = scripMapper.getScripDetail(id);
        if (scripDetail == null) {
            return ResponseData.failure(400, "未找到指定的纸条");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("mood",Integer.parseInt(scripDetail.getMood().getCode()));
        map.put("sentence",scripDetail.getSentence());
        map.put("content",scripDetail.getContent());
        return new ResponseData(200, "查找成功", map);
    }
}
