package org.example.moodvine_backend.service;

import org.example.moodvine_backend.cache.IGlobalCache;
import org.example.moodvine_backend.mapper.UserMapper;
import org.example.moodvine_backend.model.DTO.LoginData;
import org.example.moodvine_backend.model.DTO.RegisterData;
import org.example.moodvine_backend.model.PO.Gender;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.VO.JwtResponse;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.utils.Const;
import org.example.moodvine_backend.utils.EmailUtil;
import org.example.moodvine_backend.utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    IGlobalCache iGlobalCache;

    @Autowired
    UserMapper userMapper;

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
}
