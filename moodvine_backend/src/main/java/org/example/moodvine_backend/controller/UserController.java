package org.example.moodvine_backend.controller;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.annotation.CustomParam;
import org.example.moodvine_backend.model.DTO.LoginData;
import org.example.moodvine_backend.model.DTO.RegisterData;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/hello")
    public ResponseData hello() {
        System.out.println("------hello, user-------");
        return new ResponseData(200, null, "ok");
    }

    @PostMapping("/test")
    public ResponseData test(@CustomParam Integer id) {
        return ResponseData.success(id);
    }

    @PostMapping("/addScore")
    public ResponseData addScore(@CurrentUser User user,@CustomParam Integer addScore) {
        return userService.addScore(user.getId(),addScore);
    }

    @PostMapping("/wxlogin")
    public ResponseData wxLogin(@RequestBody Map<String, String> requestData) {
        String code = requestData.get("code");
        return userService.wxLogin(code);
    }

    @PostMapping("/adminLogin")
    public ResponseData adminLogin(@RequestBody LoginData loginData) {
        return userService.adminLogin(loginData);
    }

//    @PostMapping("/login")
//    public ResponseData login(@RequestBody LoginData loginData) {
//        return userService.login(loginData);
//    }
//
//    @PostMapping("/register")
//    public ResponseData register(@RequestBody RegisterData registerData) {
//        return userService.register(registerData);
//    }
//
//    @PostMapping("/sendEmail")
//    public ResponseData sendEmail(@CustomParam String email) {
//        return userService.sendEmail(email);
//    }

//    @PostMapping("/changeAvatar")
//    public ResponseData changeAvatar(@CurrentUser User user, MultipartFile file) {
//        return userService.changeAvatar(user, file);
//    }

//    @PostMapping("/changeGender")
//    public ResponseData changeGender(@CurrentUser User user, @CustomParam String gender) {
//        return userService.changeGender(user, gender);
//    }
//
//    @PostMapping("/changeSignature")
//    public ResponseData changeSignature(@CurrentUser User user, @CustomParam String signature) {
//        return userService.changeSignature(user, signature);
//    }



}
