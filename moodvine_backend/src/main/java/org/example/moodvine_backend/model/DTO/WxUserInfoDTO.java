package org.example.moodvine_backend.model.DTO;

import org.example.moodvine_backend.model.PO.Gender;

public class WxUserInfoDTO {
    private String nickName;
    private String avatar;
    private String gender;

    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {this.nickName = nickName;}
    public String getAvatar() {return avatar;}
    public void setAvatar(String avatar) {this.avatar = avatar;}
    public String getGender() {return gender;}
    public void setGender(String gender) {this.gender = gender;}
}
