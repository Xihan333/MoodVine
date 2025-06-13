package org.example.moodvine_backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final ChatClient chatClient;

    @PostMapping("/hello")
    public ResponseData hello() {
        System.out.println("------hello, user-------");
        return new ResponseData(200, "ok", "hello");
    }





}
