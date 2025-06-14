package org.example.moodvine_backend.utils;

import org.example.moodvine_backend.cache.IGlobalCache;
import org.example.moodvine_backend.mapper.UserMapper;
import org.example.moodvine_backend.model.PO.User;
import org.example.moodvine_backend.model.PO.UserType;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//注册组件
@Component
//@Data和@ConfigurationProperties结合使用用于在yaml中对其常量进行注入
@Data
@ConfigurationProperties("jwt.data")
@Slf4j
public class JwtUtil {


    @Autowired
    private IGlobalCache iGlobalCache;

    @Autowired
    private UserMapper userMapper;

    //@Value("${jwt.data.secret}")
    private String SECRET = "SECRET";//创建加密盐

    //过期时间
    //@Value("${jwt.data.expiration}")
    private Long expiration = 604800L;

    // 根据用户对象生成token new！！！！
    public String generateToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        String identifier = null;
        if (user.getEmail() != null) {
            identifier = user.getEmail();
        } else if (user.getOpenId() != null) {
            identifier = user.getOpenId();
        }

        if (identifier == null) {
            throw new IllegalArgumentException("User must have either an email or an open_id for token generation.");
        }

        claims.put(Claims.SUBJECT, identifier);
        claims.put(Claims.ISSUED_AT, new Date());

        // token储存到redis
        String token = createToken(claims);
        iGlobalCache.saveToken(identifier, token, expiration); // Use identifier as key in Redis
        return token;
    }

    // 旧的根据email生成token的方法
    public String generateToken(String email) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT, email);
        claims.put(Claims.ISSUED_AT, new Date());
        // token储存到redis
        String token = createToken(claims);
        iGlobalCache.saveToken(email, token, expiration);
        return token;
    }

    //根据token获取主题（可以是email或open_id）
    public String getSubjectFromToken(String token) {
        String subject = null;
        try {
            Claims claims = getClaimsFromToken(token);
            subject = claims.getSubject();
        } catch (Exception e) {
            log.info("error: {}", "can not find subject from token", e);
        }
        return subject;
    }

    // 根据token获取用户身份
    public UserType getUserTypeFromToken(String token) {
        String identifier = getSubjectFromToken(token);
        if (identifier == null) {
            return null;
        }
        User user = userMapper.findByEmail(identifier);
        if (user == null) {
            user = userMapper.findByOpenId(identifier);
        }
        return user != null ? user.getUserType() : null;
    }

    //从token中获取荷载
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            claims.setExpiration(new Date((long) claims.get(Claims.ISSUED_AT) + expiration * 1000));
//            System.out.println("claims的isa:" + claims.getIssuedAt());
//            System.out.println("claims的nbf:" + claims.getNotBefore());
//            System.out.println("claims的exp:" + claims.getExpiration());
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException |
                 IllegalArgumentException e) {
            log.info("###token error###", e);
        }
        return claims;
    }


    //根据负载生成jwt token
    private String createToken(Map<String, Object> claims) {
        //jjwt构建jwt builder
        //设置信息，过期时间，signature
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    //生成token失效时间
    private Date expirationDate() {
        //失效时间为：系统当前毫秒数+我们设置的时间（s）*1000=》毫秒
        //其实就是未来7天
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    //判断token是否有效
    public boolean validateToken(String token) {
        //判断token是否过期
        //判断token是否和userDetails中的一致
        //我们要做的 是先获取用户邮箱
        String identifier = getSubjectFromToken(token);
//        System.out.println("validateToken   " + identifier + "   ----------------");
        if (identifier == null) {
            return false;
        }
        String storedToken = iGlobalCache.getToken(identifier);
//        System.out.println("identifier:" + identifier);
//        System.out.println("token:" + token);
//        System.out.println("storedToken:" + storedToken);
//        System.out.println(storedToken != null);
//        System.out.println(Objects.equals(storedToken, token));
//        System.out.println(isNotTokenExpired(token));

        return storedToken != null &&
                storedToken.equals(token) &&
                isNotTokenExpired(token);
    }

    //判断token是否失效
    private boolean isNotTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate != null && !expiredDate.before(new Date());
    }

    //从荷载中获取时间
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

    //判断token是否可以被刷新
    public boolean canBeRefreshed(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate != null && expiredDate.before(new Date()); // Token is expired but can be refreshed if it was structurally valid
    }

    //刷新token
    public String refreshToken(String token) throws Exception {
        if (!canBeRefreshed(token)) {

            throw new Exception("Token cannot be refreshed. It might be expired or structurally invalid.");
        }

        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            throw new Exception("Invalid token structure for refresh.");
        }

        // 获取旧的identifier
        String oldIdentifier = claims.getSubject();
        if (oldIdentifier == null) {
            throw new Exception("Old token has no subject identifier.");
        }

        // 修改为当前时间
        claims.put(Claims.ISSUED_AT, new Date());
        String refreshedToken = createToken(claims);

        // 删除 Redis 中之前的 Token
        String oldStoredToken = iGlobalCache.getToken(oldIdentifier);
        if (oldStoredToken == null) {
            throw new Exception("Original token not found in Redis for refresh.");
        }
        iGlobalCache.deleteToken(oldIdentifier);

        // 更新 Redis 中的 Token
        iGlobalCache.saveToken(oldIdentifier, refreshedToken, expiration);

        return refreshedToken;
    }

    public String extractTokenFromHeader(String authorizationHeader) {
//        System.out.println(authorizationHeader != null);
//        System.out.println(authorizationHeader.startsWith("Bearer "));
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // 去掉 "Bearer " 前缀
        } else {
            return authorizationHeader;// 如果不带前缀或为空，则返回 null
        }
    }

}

