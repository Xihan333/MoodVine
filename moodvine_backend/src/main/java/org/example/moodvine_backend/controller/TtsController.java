package org.example.moodvine_backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.moodvine_backend.annotation.CustomParam;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.TtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tts")
@RequiredArgsConstructor
public class TtsController {

    @Autowired
    TtsService ttsService;

    @Operation(summary = "文本转语音")
    @PostMapping("/speech")
    public ResponseData Speech(@CustomParam String text) throws Exception {
        return ttsService.speech(text);
    }
}
