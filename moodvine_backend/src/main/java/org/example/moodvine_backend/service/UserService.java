package org.example.moodvine_backend.service;

import org.example.moodvine_backend.annotation.CurrentUser;
import org.example.moodvine_backend.cache.IGlobalCache;
import org.example.moodvine_backend.mapper.*;
import org.example.moodvine_backend.model.DTO.LoginData;
import org.example.moodvine_backend.model.DTO.RegisterData;
import org.example.moodvine_backend.model.PO.*;
import org.example.moodvine_backend.model.VO.JwtResponse;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.utils.Const;
import org.example.moodvine_backend.utils.EmailUtil;
import org.example.moodvine_backend.utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.moodvine_backend.utils.NickNameGenerator;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;


@Service
public class UserService {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    IGlobalCache iGlobalCache;

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiaryMapper diaryMapper;

    @Autowired
    ScripMapper scripMapper;

    @Autowired
    MoodMapper moodMapper;

    @Autowired
    TabMapper tabMapper;

    @Autowired
    ClockInActivityMapper clockInActivityMapper;

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.secret}")
    private String secret;

    public ResponseData wxLogin(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("WeChat API raw response: " + result);
            // 使用FastJSON解析响应
            Map<String, String> resultMap = JSON.parseObject(result, new TypeReference<Map<String, String>>() {});
            System.out.println("Parsed resultMap: " + resultMap);
            String openid = resultMap.get("openid");
            System.out.println("Extracted openid: " + openid);
            String sessionKey = resultMap.get("session_key");
            
            if (openid == null || openid.isEmpty()) {
                return ResponseData.failure(401, "微信登录失败");
            }

                // 检查用户是否已注册
                User user = userMapper.findByOpenId(openid);
                if (user == null) {
                // 用户未注册，创建新用户
                    user = new User();
                    user.setOpenId(openid);
                    user.setUserType(UserType.USER);
                    user.setScore(0);
                    user.setAvatar("");

                    String randomNickname = NickNameGenerator.generateRandomNickname();
                    user.setNickName(randomNickname);
                    userMapper.insert(user);

                    user = userMapper.findByOpenId(openid);
                }

                // 生成 JWT 令牌
            String token = jwtUtil.generateToken(user);
            
            // 创建返回数据
            Map<String, Object> responseData = new HashMap<>();
                responseData.put("token", token);
            responseData.put("user", user);
            responseData.put("isNewUser", user.getEmail() == null); // 如果是新用户，email为null
            
                return ResponseData.success(responseData);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseData.failure(500, "服务器内部错误");
        }
    }

    private Map<String, String> parseJson(String json) {
        // 实现 JSON 解析逻辑
        Map<String, String> resultMap = new HashMap<>();
        String[] pairs = json.replace("{", "").replace("}", "").split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                resultMap.put(keyValue[0].replace("\"", "").trim(), keyValue[1].replace("\"", "").trim());
            }
        }
        return resultMap;
    }

    public ResponseData adminLogin(LoginData loginData) {
        // 获取用户输入的邮箱和密码
        String email = loginData.getEmail();
        String password = loginData.getPassword();

        // 查看该邮箱是否注册过
        boolean userExist = userMapper.judgeExistsByEmail(email);
        if (!userExist) {
            return ResponseData.failure(401, "该邮箱未被注册！");
        }

        // 从数据库中查询用户信息
        User user = userMapper.findByEmail(email);

        // 校验用户是否为管理员
        if (user.getUserType() != UserType.ADMIN) {
            return ResponseData.failure(401, "您不是管理员，无法登录！");
        }

        // 校验用户是否存在以及密码是否匹配
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return ResponseData.failure(401, "邮箱或密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user);

        // 创建JwtResponse对象并设置令牌
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(token);
        jwtResponse.setEmail(email);

        // 返回包含JWT令牌的响应实体
        return ResponseData.success(jwtResponse);
    }


    private static final Logger logger = LoggerFactory
            .getLogger(UserService.class);

//    @Autowired
//    private JavaMailSender javaMailSender;
//
//    @Value("${spring.mail.username}")
//    String fromEmail;
//
//    @Value("${web.upload-path}")
//    String path;
//
//    @Value("${web.image-path}")
//    String imgPath;
//
//
//    public ResponseData login(LoginData loginData) {
//
//        // 获取用户输入的邮箱和密码
//        String email = loginData.getEmail();
//        String password = loginData.getPassword();
//
//        // 查看该邮箱是否注册过
//        boolean userExist = userMapper.judgeExistsByEmail(email);
//        if(!userExist) {
//            return ResponseData.failure(401,"该邮箱未被注册！");
//        }
//
//        // 从数据库中查询用户信息
//        User user = userMapper.findByEmail(email);
////        System.out.println("找到了用户");
//
//        // 校验用户是否存在以及密码是否匹配
//        if(!BCrypt.checkpw(password, user.getPassword())) {
//            return ResponseData.failure(401, "邮箱或密码错误");
//        }
//
//        // 生成JWT令牌
//        String token = jwtUtil.generateToken(loginData.getEmail());
//
//        // 创建JwtResponse对象并设置令牌
//        JwtResponse jwtResponse = new JwtResponse();
//        jwtResponse.setToken(token);
//        jwtResponse.setEmail(email);
//
//        // 返回包含JWT令牌的响应实体
//        return ResponseData.success(jwtResponse);
//    }
//
//    public ResponseData register(RegisterData registerData) {
//        String email = registerData.getEmail();
//
//        // 查看该邮箱是否注册过
//        boolean userExist = userMapper.judgeExistsByEmail(email);
//        if(userExist) {
//            return ResponseData.failure(401,"该邮箱已注册过！");
//        }
//
//        if (!email.contains("@")) return ResponseData.failure(401,"邮箱格式错误！");
//        // 校验邮件验证码
//        String redisIKey = "MailVerificationCode-" + email;
//        String trueCode = (String) iGlobalCache.get(redisIKey);
//        if (trueCode == null || !trueCode.equals(registerData.getMailCode())) {
//            return ResponseData.failure(401,"验证码错误！");
//        }
//        User user = new User();
//        String encodedPassword = BCrypt.hashpw(registerData.getPassword(), BCrypt.gensalt());
////        String encodedPassword = encoder.encode(registerData.getPassword());
//        user.setPassword(encodedPassword);
//        user.setEmail(email);
//        user.setNickName(registerData.getNickName());
//        userMapper.insert(user);
//
//        return ResponseData.ok();
//    }
//
//    public ResponseData sendEmail(String email) {
//        logger.info("HTML email sending start");
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        // 生成邮件验证码
//        String mailVerificationCode = EmailUtil.generateRandomString(6);
//        System.out.println("成功生成验证码 " + mailVerificationCode);
//        System.out.println(email);
//        System.out.println("--------------------------------");
////        String email = dataRequest.getString("email");
//        try {
//            // Set multipart mime message true
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
//                    true);
//            mimeMessageHelper.setFrom(fromEmail);
//
//            System.out.println(email);
//            mimeMessageHelper.setTo(email);
//
//            String html = "<html>"
//                    + "<head>"
//                    + "    <style>"
//                    + "        body {"
//                    + "            font-family: Arial, sans-serif;"
//                    + "            background-color: #f5f5f5;"
//                    + "            padding: 20px;"
//                    + "        }"
//                    + "        h1 {"
//                    + "            color: #333;"
//                    + "            text-align: center;"
//                    + "        }"
//                    + "        .container {"
//                    + "            text-align:center;"
//                    + "            width: 90%;"
//                    + "            height: 50%"
//                    + "            margin: 0 auto;"
//                    + "            background-color: #fff;"
//                    + "            padding: 20px;"
//                    + "            border-radius: 5px;"
//                    + "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);"
//                    + "        }"
//                    + "        .code.b {"
//                    + "            color: darkseagreen;"
//                    + "            font-size: 300%;"
//                    + "            border-radius: 3px;"
//                    + "        }"
//                    + "    </style>"
//                    + "</head>"
//                    + "<body>"
//                    + "    <div class='container'>"
//                    + "        <h1>欢迎来到Musical</h1>"
//                    + "        <p class='code'>验证码<b>" + mailVerificationCode + "</b> </p>"
//                    + "        <p>您的操作需要您提供接收到的验证码，验证码在5分钟内有效。</p>"
//                    + "        <p>如果您没有进行过获取验证码的操作，请忽略这封邮件。</p>"
//                    + "        <p>祝您生活愉快！</p>"
//                    + "    </div>"
//                    + "</body>"
//                    + "</html>";
//
//            mimeMessageHelper.setText(html, true);
//
//            javaMailSender.send(mimeMessage);
//
//        } catch (MessagingException e) {
//            logger.error("Exeception=>sendHTMLEmail ", e);
//        }
//        // 存入Redis
//        String redisIKey = "MailVerificationCode-" + email;
//        try {
//            iGlobalCache.set(redisIKey, mailVerificationCode, Const.MAIL_VERIFICATION_CODE_EXPIRE_TIME);
//            System.out.println("验证码成功存入redis！");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return ResponseData.failure(401, "Redis存储异常");
//        }
//        return ResponseData.ok();
//    }

//    public ResponseData changeAvatar(User user, MultipartFile file) {
//        String avatar;
//        if (file != null) {
//            String fileName = FileUtil.upload(file, path, file.getOriginalFilename());
//            if (fileName != null) { avatar = imgPath + fileName;  //提取图片地址
//            } else {
//                return ResponseData.failure(400,"这不是有效的文件!");
//            }
//        } else {
//            return ResponseData.failure(403,"文件不能为空!");
//        }
//
//        user.setAvatar(avatar);
//        userMapper.update(user);
//        return ResponseData.success(user);
//    }

//    public ResponseData changeGender(User user, String gender) {
//        user.setGender(Gender.valueOf(gender));
//        userMapper.update(user);
//        return ResponseData.ok();
//    }
//
//    public ResponseData changeSignature(User user, String signature) {
//        user.setSignature(signature);
//        userMapper.update(user);
//        return ResponseData.ok();
//    }
    public ResponseData addScore(Integer userId, Integer addScore){
        userMapper.addScore(userId,addScore);
        return ResponseData.ok().msg("成功增加").data(Collections.emptyMap());
    }

    public ResponseData updateWxUserInfo(
            @CurrentUser User user,
            String nickName,
            String avatar
    ) {
        if (user == null) {
            return ResponseData.failure(401, "用户未登录");
        }

        if (nickName != null) user.setNickName(nickName);
        if (avatar != null) user.setAvatar(avatar);

        userMapper.update(user);
        return ResponseData.success(user);
    }

    public ResponseData getUserInfo(User user) {
        if (user == null) {
            return ResponseData.failure(401, "用户未登录");
        }
        Map<String, Object> data = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        data.put("nickname",user.getNickName());
        data.put("gender",Integer.parseInt(user.getGender().getCode()));
        if(user.getBirthday()!=null){
            data.put("birthday",sdf.format(user.getBirthday()));
        }
        else {
            data.put("birthday",null);
        }
        data.put("avatar",user.getAvatar());
        data.put("email",user.getEmail());
        data.put("score",user.getScore());
        return ResponseData.ok().data(data).msg("查询成功");
    }

    public ResponseData updateUserInfo(User user, String nickName, String avatar, Gender gender,String email, String birthday) {
        if (user == null) {
            return ResponseData.failure(401, "用户未登录");
        }
        if (nickName != null) user.setNickName(nickName);
        if (avatar != null) user.setAvatar(avatar);
        if (gender != null) user.setGender(gender);
        if (email != null) user.setEmail(email);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (birthday != null) {
            try {
                Date date = sdf.parse(birthday);
                user.setBirthday(date);
            } catch (Exception e) {
                return ResponseData.failure(400, "生日格式不正确，正确格式为 yyyy-MM-dd");
            }
        }
        userMapper.updateUserInfo(user);
        return ResponseData.ok().msg("修改成功").data(Collections.emptyMap());
    }

    public ResponseData getWeeklyStatistics(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(DayOfWeek.MONDAY);
        LocalDate endDate = today.with(DayOfWeek.SUNDAY);
        System.out.println(startDate);
        System.out.println(endDate);

        // 统计本周日记的个数
        Integer diaryCount = diaryMapper.getDiariesByDateRange(userId, startDate, endDate);

        // 统计本周聊愈纸条scrip的次数
        Integer scripCount = scripMapper.getScripsByDateRange(userId, startDate, endDate);

        // 统计本周最频繁的心情
        List<Mood> moods = moodMapper.findMoodByDateRange(userId, startDate, endDate);
        String mostFrequentMood = getMostFrequentMood(moods);

        // 统计本周活动打卡次数
        Integer clockInCount = clockInActivityMapper.getClockInByDateRange(userId, startDate, endDate);

        // 统计本周所有的tab标签
        List<Tab> tabs = tabMapper.getTabsByUserAndDateRange(userId, startDate, endDate);
        List<Map<String,Object>> Labels = tabs.stream().map(tab -> {
            Map<String,Object> map = new HashMap<>();
            map.put("content",tab.getContent());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("diaryCount", diaryCount);
        statistics.put("scripCount", scripCount);
        statistics.put("mostFrequentMood", Integer.parseInt(mostFrequentMood));
        statistics.put("activityClockIn", clockInCount);
        statistics.put("tabs", Labels);

        return ResponseData.success(statistics);
    }

    private String getMostFrequentMood(List<Mood> moods) {
        if (moods.isEmpty()) {
            return null;
        }
        Map<String, Integer> moodCount = new HashMap<>();
        for (Mood mood : moods) {
            String moodCode = mood.getMood().getCode();
            moodCount.put(moodCode, moodCount.getOrDefault(moodCode, 0) + 1);
        }
        String mostFrequent = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : moodCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }
        return mostFrequent;
    }

}
